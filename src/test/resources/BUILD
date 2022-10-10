load("//libs/bazel/java:rules_java.bzl", "generic_java_library")

generic_java_library(
    name = "resources",
    resources = glob(
        ["**/*"],
        exclude = ["BUILD"],
    ),
    visibility = [
        "//platform/libs/flower/src/test:__subpackages__",
    ],
)
