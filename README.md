Flower
========

Flower is a lightweight workflow engine.

[Introduction](#introduction)

[Example: "Hello world" flow](#example-1-hello-world-flow)

[Example: Initializing Flower and running an instance of "Hello world" flow](#example-2-initializing-flower-and-running-an-instance-of-hello-world-flow)

[Flows and Steps](#flows-and-steps)

[Step Function and Transit Function](#step-function-and-transit-function)

[Flow and Function names](#flow-and-function-names)

[Flow State](#flow-state)

[Functions and parameters](#functions-and-parameters)

[Parameter passing: defaults](#parameter-passing-defaults)

[Reusing Transit Functions](#reusing-transit-functions)

[Global Functions and Global Function Calls](#global-functions-and-global-function-calls)


## Introduction

Flower as a library provides a set of Java Annotations with interpreter (Flower Engine) that allow to define program structure in the form of Flows.
Flower is based on Guava Listenable Futures https://guava.dev/releases/21.0/api/docs/com/google/common/util/concurrent/Futures.html.

The idea behind Flower is to implement a model that can be used for better structuring of complex, multi-step processes. Such processes typically have plenty of logic related to reiterating, retrying, throttling, waiting, delaying, analyzing failures and trying again; going back a step or two, or handling reaching a state at which it's best to roll back everything that has been done prior.

Workflows of this kind are common to many different fields - they can be found in management of clusters, large horizontally scalable systems, in environments with many components each of which can fail at any time and no link or connection or service can be considered 100% reliable. Unsurprisingly, we also find quite a few examples of such logic in the space of platform integrations, where each service provider has its own complex systems with their own rules and our goal is to put them together and accommodate for all potential special cases, including, but not limited to aggressive throttling, changes of throughput and latency at different times of day or different days, and simply random network issues, so that everything works in the most reliable fashion and any problem is next to invisible, according to our customer's user experience.

Flower was designed with this in mind and offers a model that allows to define complex Flows made of large number of Steps, easily manipulate transitioning between Steps, analyze execution results and errors and react accordingly, implement retrial strategies like exponential wait, etc. The core idea of this model is to decouple work logic of Step from logic of analysis of the results of Step's execution and the structure of transitions between Steps in a Flow; on the other hand allowing to implement and run subtasks as child Flows. 

On top of that, Flower programming model presents other noteworthy properties:
- Execution structure of a Flow is clearly outlined and can be automatically visualized as a directed graph;
- Operating state for each Flow, as well as Inputs and Outputs for each Function are well defined, providing better readability and testability;
- Child Flows allow to define parallel branches and trees of execution without sacrificing the overall clarity of visualizable execution structure;
- Flow Inheritance and support for Java generics allow to easily slice/dice and create variations of existing Flows for better versatility;
- Event Profiles feature provides means to program Flow execution control routines, like automated logging, metrics collecting; and can be used for less standard tasks - Flow state persistence, etc.;
- Completely non-blocking execution model, based on ListenableFutures;
- Can support large number of parallel Flows, all of them executing on a limited number of threads in a ThreadPool;
- Flow sleep/execution delays are trivial with Transition feature without blocking any threads;
- No boilerplate required to wait for Listenable Future completion and extract outputs;

It's important to note, that while Flower model does not prohibit distributed execution on a cluster of machines, the current version of Flower Engine is implemented as a lightweight library that doesn't provide clustering features out of the box, and it's not in our current milestones. The reasons are that right now we're in the earliest phase in the development of technology that can grow into something much bigger, and the main goal is to prove beyond any doubt that the model works well for its purpose, and works well on what can be referred to as "micro-level", being used as a structuring layer on top of Listenable Futures for the class of tasks that are usually programmed directly with Listenable Futures, not in the last place because we have very good use cases to utilize Flower in this capacity. Should this effort prove successful, there would be no better incentive to make it clustered and horizontally scalable. This early phase is also important because what is emerging here can be viewed as a programming paradigm very different from anything else I've seen before, and the value of proving that it works as a concept can't be underestimated.

Some of my far-fetched thoughts and dreams of how Flower's model can change current architectural patterns when applied on a mesh scale can be found in Appendix. **TODO: Appendix with idea about replacing microservice architecture pattern**


## Example 1: "Hello world" flow

We start with 2 code examples, so we can use the later as a reference. 
The first example is a simple Flow defined with Flower annotations. When an instance of this Flow is executed, it outputs (rather unexpectedly) "Hello world!".

     1 |  @FlowType(firstStep = "HELLO_STEP")
     2 |  public class HelloWorldFlow {  
     3 |    @State final String hello = "Hello";  
     4 |    @State String world;  
     5 |    
     6 |    @SimpleStepFunction  
     7 |    static Transition HELLO_STEP(@In String hello,  
     8 |                                 @Out OutPrm<String> world,  
     9 |                                 @StepRef Transition WORLD_STEP) {  
    10 |      System.out.print(hello);  
    11 |      world.setOutValue(" world!");  
    12 |      return WORLD_STEP;  
    13 |    }  
    14 |    
    15 |    @SimpleStepFunction  
    16 |    static Transition WORLD_STEP(@In String world,  
    17 |                                 @Terminal Transition END) {  
    18 |      System.out.println(world);  
    19 |      return END;  
    20 |    }
    21 |  }


## Example 2: Initializing Flower and running an instance of "Hello world" flow

In the following example we initialize Flower engine by registering HelloWorldFlow class, then get a corresponding `FlowExec` from Flower engine, create an instance of our Flow and run it. As the result of execution we can obtain the final state of our Flow instance:

     1 |  void testMe() throws ExecutionException, InterruptedException {
     2 |    Flower flower = new Flower();
     3 |    flower.registerFlow(HelloWorldFlow.class);
     4 |    flower.initialize();
     5 |    
     6 |    FlowExec<HelloWorldFlow> helloWorldExec = flower.getFlowExec(HelloWorldFlow.class);
     7 |    FlowFuture<HelloWorldFlow> flowFuture = helloWorldExec.runFlow(new HelloWorldFlow());
     8 |    
     9 |    HelloWorldFlow state = flowFuture.getFuture().get();
    10 |    assertEquals(state.hello, "Hello");
    11 |    assertEquals(state.world, " world!");
    12 |  }


## Flows and Steps

Flows consist of Steps. 
One of the Steps is marked as a `firstStep`, essentially becoming an entry point for our Flow. 

Each Step has 2 responsibilities: 
1) to execute custom logic related to the Step; 
2) to indicate which Step should execute after it (or terminate the Flow execution).

This gives us the basic picture of Flow execution - we run the Step that's defined as `firstStep`, and then follow the chain of Transitions, executing Step after Step until we reach terminal state.

From Java perspective, we represent a Flow as a class, and in its simplest form a Step is represented as a static function of that class, which returns a special `Transition` object. Flow class is annotated as `@FlowType`, and first Step is defined by specifying its name in annotation's element `firstStep`.
In our "HelloWorld" flow it looks like this:

    @FlowType(firstStep = "HELLO_STEP")

The simplest way to mark a static method of a Flow as its Step would be to annotate the method with `@SimpleStepFunction`. Please note, that in this case the method must return either `Transition` or `ListenableFuture<Transition>`, which is required due to the 2nd responsibility of a Step - to define which Step should execute after it.
Name of a Step can optionally be specified in annotation, otherwise Flower gets corresponding method name via reflection and uses that string as Step's name. 


## Step Function and Transit Function

As it was mentioned earlier, Step as an entity has 2 responsibilities, and it's not ideal when it's represented as a single `@SimpleStepFunction`. To address this problem, it's possible to define a Step as a pair of functions - `@StepFunction` and `@TransitFunction`.
Here's an example of how a `HELLO_STEP` function from [example 1](#example-1-hello-world-flow) can be split:

     1 |  @StepFunction(transit = "HELLO_TRANSIT")
     2 |  static void HELLO_STEP(@In String hello,
     3 |                         @Out OutPrm<String> world) {
     4 |    System.out.print(hello);
     5 |    world.setOutValue(" world!");
     6 |  }
     7 |
     8 |  @TransitFunction
     9 |  static Transition HELLO_TRANSIT(@StepRef Transition WORLD_STEP) {
    10 |    return WORLD_STEP;
    11 |  }
    
In this case `@TransitFunction` will be executed right after `@StepFunction` (i.e. if Step function returns a `ListenableFuture`, after that Future completes). The main reason for implementing this split is to provide a mechanism to reuse both parts, importantly - Transitioner part. We'll discuss this in further detail after function parameters are covered, in section [Reusing Transit Functions](#reusing-transit-functions).


## Flow and Function names

There are reasons to reference Flows and Functions in Flower. Examples of such references so far are identifying first Step in a Flow, e.g.:
`@FlowType(firstStep = "HELLO_STEP")`
or referencing a TransitFunction from StepFunction, e.g.:
`@StepFunction(transit = "HELLO_TRANSIT")`.

In the previous sections we didn't specify Flow and Function names explicitly, but it's not hard to do:
`@FlowType(name = "TEST_FLOW", ...)`
`@SimpleStepFunction(name = "HELLO_STEP", ...)`
`@StepFunction(name = "HELLO_STEP", ...)`
`@TransitFunction(name = "HELLO_TRANSIT", ...)`

Names in those annotations are optional, if they're not specified Flower is using reflection to assign names.
`@FlowType` - Java fully qualified class name, i.e. package.class;
`@SimpleStepFunction`, `@StepFunction`, `@TransitFunction` - Java method name.

Please note that Step Name is the same as `@SimpleStepFunction` or `@StepFunction` name.
For example, first step in a flow is defined by Step Name:
`@FlowType(firstStep = "HELLO_STEP")`


## Flow State

As you can see from our [example 2](#example-2-initializing-flower-and-running-an-instance-of-hello-world-flow) Line 7, method `FlowExec.runFlow(...)`  as a parameter takes an object of a Flow, rather than Flow class. This relates to the fact that Flower engine is executing *Flow Instances*, rather than Flows. Since in Java we define our Flows as classes, Flow Instances are instances, or objects of those classes.

To avoid confusion, let me indicate that we refer to the classes as *Flows* or *Flow Classes*, and to objects of those classes as *Flow Instances* or *Flow Objects*.

Flow, being a Java class, defines a structure of fields that is common to every instance, or object of that Flow. In [example 1](#example-1-hello-world-flow) Line 3-4, you can see that Flow Fields are annotated with `@State`:

    @State final String hello = "Hello";  
    @State String world;  

This annotation means that given Flow Field is a part of Flow's State and as such can be injected *into and out of* parameters of functions related to its Flow. One example of such function related to a Flow is a `@SimpleStepFunction`.

What exactly "injected *into and out of*" means is explained in the section [Flow State parameters](#flow-state-parameters).


## Functions and parameters

Going back to our [example 1](#example-1-hello-world-flow), you can see that our simple step functions `HELLO_STEP` and `WORLD_STEP` take a few parameters, and those parameters have annotations attached.
Let's discuss those annotations and their meaning.

### Flow State parameters

Flow state-related parameters allow function to interact with Flow state:

- **@In**: Input function parameter. Maps State field to a parameter. 
It means that we inject the current value stored in State Field to this parameter in our function; 

- **@Out**: Output function parameter. Maps output parameter to a State Field. 
It means that our function provides new value to update State Field with, after the function executed.
The corresponding function parameter must be of type `OutPrm<StateFieldType>`,  and allows to set output either as a value, or as a ListenableFuture (methods `setOutValue(...)` and `setOutFuture(...)`);

- **@InOut**: Combines the features of both `@In` and `@Out`, i.e. gives access to current value from State Field, and also allows to update the value of that State Field after the function executed.
The corresponding function parameter must be of type `InOutPrm<StateFieldType>`. To get the current value of State Field, use method `getInValue()`. Similarly to `OutPrm`, methods `setOutValue(...)` and `setOutFuture(...)` allow to update value of a State Field.

### Transitioner parameters

Parameters `@InRet` and `@InRetOrException` can only be used in `@TransitFunction`, while `@StepRef` and `@Terminal` can be used in both `@TransitFunction` and `@SimpleStepFunction`.

- **@InRet**: Injects a value returned by `@StepFunction` (`@TransitFunction` only);

- **@InRetOrException**: Injects a value returned by `@StepFunction` or an Exception if it occurred during execution of `@StepFunction` (`@TransitFunction` only);

- **@StepRef**: defines possible transition to a Step;

- **@Terminal**: defines possible transition to a Terminal state (Flow Instance terminates).

### Flower engine parameters

Those parameters can be used only in `@StepFunction` and `@SimpleStepFunction`.

- **@FlowRepo**: Represents a reference to a Flow repository to query `FlowFuture`-s. 
The corresponding function parameter must be of type `FlowRepoPrm`.

- **@FlowFactory**: allows to run a child flow of a current flow. Similarly to `FlowExec` in [example 2](#example-2-initializing-flower-and-running-an-instance-of-hello-world-flow) the factory is created for a specific FlowType.
The corresponding function parameter must be of type `FlowFactoryPrm<FlowClass>`. 
Note: Flow Factory extends Flow Repo, so it can also be used to monitor the state of child flows.

WARNING: Please Do **NOT** use objects `FlowExec` or `Flower` directly inside Flows. As a result of such usage, the information about parent-child relationships in Flows will be lost. (Both information about possible child Flows in Flow Classes, and actual parent-child references in Flow Instances). The latter would reduce available information about execution context for child Flow Objects, while the former would make parent-child cycle detection and diagramming inefficient.
**TODO: @StepFunction(..., returnTo = "fromFlowerStr")**


## Parameter passing: defaults

Every function parameter has a name. Parameter names are used to map parameters in Global Function Calls, which we will discuss in section **TODO: ADD SECTION FOR GLOBAL FUNCTIONS AND CALLS**.
Similarly to [Flow and Function names](#flow_and_function_names), parameter names can be assigned explicitly in annotations, e.g.:
`@In(name = "hello, ...) String hello`

The following parameter annotations have other optional elements with default values:
- **@In**, **@InFromFlow**: 
	- from: from State field, defaults to Java parameter name;
- **@Out**, **@OutFromFlow**: 
	- to: to State field, defaults to Java parameter name;
- **@InOut**, **@InOutFromFlow**: 
	- fromAndTo: from and to State field, defaults to Java parameter name;
- **@StepRef**:
	- stepName: name of a Step, defaults to Java parameter name.

The idea of those defaults is to keep our code concise and clean: for instance, if we've already defined a name for Java method parameter, then if possible we would like to avoid redefining it in annotation elements, implicitly assuming the same name for some annotation properties. The only good reason to explicitly set those properties would be in situations when an annotation element must be assigned some other value. As practice shows, default values suffice in vast majority of cases.
On top of that, when names of entities match - it makes referenced entities easier to find in the code.

Parameter annotations `@InFromFlow`, `@OutFromFlow`, `@InOutFromFlow` will be discussed in section **TODO: ADD RE TO SECTION FOR EVENT PROFILES**.

**TODO: IN / OUT / IN_OUT  ADD SECTION FOR TYPE COMPARISON? OR REFERENCE TO THAT SECTION?**


## Reusing Transit Functions

To illustrate some challenges connected to reusing Transition Functions let's get back to our "HelloWorld" flow, which has the following uncomplicated Flow structure.

```mermaid
graph LR
BEGIN --> HELLO_STEP
HELLO_STEP --> WORLD_STEP
WORLD_STEP --> END
```
We've previously shown the example of splitting our `HELLO_STEP` in two functions, in section [Step function and Transit Function](#step-function-and-transit-function). Now let's imagine that we want to split `WORLD_STEP` as well, and reuse the Transit Function (a.k.a. Transitioner).

It's not hard to split `WORLD_STEP` in a similar fashion:

     1 |  @StepFunction(transit = "WORLD_TRANSIT")
     2 |  static void WORLD_STEP(@In String world) {  
     3 |    System.out.println(world);  
     4 |    return END;  
     5 |  }
     6 |
     7 |  @TransitFunction
     8 |  static Transition WORLD_TRANSIT(@Terminal Transition END) {
     9 |    return END;
    10 |  }
    
But now we have a problem - as you can see from the diagram, `HELLO_STEP` transits to `WORLD_STEP`, and `WORLD_STEP` transits to `@Terminal` state. For that reason we have 2 different Transitioners:

     1 |  @TransitFunction
     2 |  static Transition HELLO_TRANSIT(@StepRef Transition WORLD_STEP) {
     3 |    return WORLD_STEP;
     4 |  }
     5 |
     6 |  @TransitFunction
     7 |  static Transition WORLD_TRANSIT(@Terminal Transition END) {
     8 |    return END;
     9 |  }
    
Having said that, both those Transit Functions perform the same exact logic (or absence of) and the only real difference between them is the parameter they're accepting. In practice, it's more likely that different Steps would transition to different next Steps, but very likely that said Steps will use the same general transition logic - which can range from something as simple as the above, to strategies like exponential waiting, or requesting some state from external services to make a decision between waiting or failing. Usually there are only a handful of strategies, and they are reused everywhere, so duplicating this logic by expressing everything as `@SimpleStepFunction` or multiple identical Transition Functions is clearly suboptimal.
 
To allow reuse of Transit Functions a feature called Transit Parameter Override was implemented. This is how the final version of the code would look for the example:

     1 |  @StepFunction(transit = "TRANSIT")
     2 |  static void HELLO_STEP(@In String hello,
     3 |                         @Out OutPrm<String> world) {
     4 |    System.out.print(hello);
     5 |    world.setOutValue(" world!");
     6 |  }
     7 |
     8 |  @TransitTerminalPrm(paramName = "WORLD_STEP")
     9 |  @StepFunction(transit = "TRANSIT")
    10 |  static void WORLD_STEP(@In String world) {  
    11 |    System.out.println(world);  
    12 |    return END;  
    13 |  }
    14 |
    15 |  @TransitFunction
    16 |  static Transition TRANSIT(@StepRef Transition WORLD_STEP) {
    17 |    return WORLD_STEP;
    18 |  }

As you can see, both Steps use the same Transitioner `TRANSIT`, and by default it transits to `WORLD_STEP`, but to `WORLD_STEP` doesn't want to transit to itself, it wants to transit to Terminal state, that's why it overrides a corresponding parameter of `TRANSIT` by using an additional annotation `@TransitTerminalPrm` (Line 8). The effect of the override is that when `TRANSIT` will be called in conjunction with Step `WORLD_STEP`, it will terminate the Flow Instance by transitioning to Terminal State.

The following Transit Parameter Overrides are available:
- **TransitInOutPrm**: overrides @InOut parameter in Transitioner with a different Flow Field;
- **TransitInPrm**: overrides @In or @InRet parameter in Transitioner with a different Flow Field;
	- **TODO: check if we can override @InRetOrException with this**
- **TransitInRetPrm**: overrides @In parameter in Transitioner with Step Function's return value;
	- **TODO: check if we can override @InRetOrException with this**
- **TransitOutPrm**: overrides @Out parameter in Transitioner with a different Flow Field;
- **TransitStepRefPrm**: overrides @StepRef or @Terminal parameter in Transitioner with a different Step Transition;
- **TransitTerminalPrm**: overrides @StepRef or @Terminal parameter in Transitioner with a Terminal state Transition;

and finally
- **TransitParametersOverride**: since we can't have more than one annotation of each kind on a method, this annotation structure allows us to override multiple Transitioner parameters of the same kind. The way it works can be seen from the code:

      @Retention(RetentionPolicy.RUNTIME)
      @Target({ElementType.METHOD})
      public @interface TransitParametersOverride {
        TransitInPrm[] in() default {};
        TransitOutPrm[] out() default {};  
        TransitInOutPrm[] inOut() default {};  
        TransitInRetPrm[] inRet() default {};  
        TransitStepRefPrm[] stepRef() default {};  
        TransitTerminalPrm[] terminal() default {};  
      }
      
This might look tricky, and definitely is trickier than I wish it would be, but the reason it's introduced is to allow Transitioner code reusability in situations when annotations overhead is reasonable and justified. Ideally, when we trade a few lines of Transit Override annotations vs dozens of lines in reusable Transitioner (perhaps, with complex logic).
There are ways to avoid using it altogether - for example extract Transition logic to a helper class, and use that method directly from the code of `@SimpleStepFunction`. However, that will make some Transitioner features unavailable - for example, auto-completing futures from StepFunction's `@Out`, `@InOut` and `@InRet`, or using `@InRetOrException` as a try-catch with multiple steps in a universal fashion. 
Later we will discuss Global Functions and Function Calls, and those features enable extended Transitioner reuse from different Flows, so one definitely might find a bit of value in Transitioner Parameter Override feature.
But, in the end, the best measure is always good sense, and I suggest you apply it every time there is a need to evaluate something that can potentially make your code less readable.


## Global Functions and Global Function Calls

The previous section mentioned Global Functions, and in the context of reusing Transitioners the benefit of being able to access common transit logic from any Flow is most obvious. While Transit Functions are a clear example of such need, the same can be true for some Step Functions. And given that, there doesn't seem to be a good reason to restrict reusing the logic from Event Functions, even though it's not likely that such reusability will be used often; just to extend the same model on all Functions. (We will discuss Event Functions later **TODO: ADD REF TO SECTION FOR EVENT PROFILES**).

Global Functions present the possibility to reuse the code fully by means of Flower, i.e. in a way that's not standard Java. There are a few reasons why such functionality was introduced in spite of the fact that all the powerful Java instruments are still available:
1) All Flower features like waiting for future completion in `@Out`, `@InOut`, `@InRet` or emulating try-catch logic in async execution with `@InRetOrException` will be available;
2) Step Functions can refer to Transit Functions just with 1 additional annotation element (minus TransitOverrides), e.g.: `@StepFunction(transit = "transit")`;
3) The Global Function Call structure is known to Flower and potentially can be used in Flow diagramming, etc.;
4) Makes Flower model more complete (super-far-fetched dreams about extending Flower model to other languages, enabling cross-language execution, or even serving as a foundation for some new programming paradigm).




More on Functions - transitions, delays, also passing future in @Out, future in return value, returnTo
Flow Inheritance
Event Profiles
Child Flows
Type System / Generics