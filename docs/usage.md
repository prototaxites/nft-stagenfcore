# Using nft-stagenfcore

This plugin requires that nf-core tools is installed in order to install modules.

## Initialise an nf-core library

In a setup block, run the `nfcoreSetup()` function to initialise an nf-core library. This function takes a string as an argument that is a path pointing to the location to set up the library. It is recommended to use `launchDir` as this will initialise a test-specific library.

```groovy
setup {
    nfcoreSetup(launchDir)
}
```

## Install modules

Use the `nfcoreInstall()` function to install nf-core modules. This function takes the path to the library and a list of strings, each with an nf-core module name in `tool/subtool` format.

```groovy
setup {
    nfcoreSetup(launchDir)
    nfcoreInstall(launchDir, ["minimap2/index", "minimap2/align"])
}
```

## Link the library to your modules directory

Use the `nfcoreLink()` function to link the library to your module library. This function takes two arguments, the path passed to `nfcoreSetup()`, and the path to your directory containing modules (e.g. `${baseDir}/modules`):

```groovy
setup {
    nfcoreSetup(launchDir)
    nfcoreInstall(["minimap2/index", "minimap2/align"])
    nfcoreLink(launchDir, "${baseDir}/modules")
}
```

## Clean up after tests are finished

To unlink the temporary library after the test has completed, use the `nfcoreUnlink()` function. It takes the path to your modules directory as an input.

You can also use the `nfcoreDeleteLibrary()` function to remove the temporary library, if desired.

```groovy

setup {
    nfcoreSetup(launchDir)
    nfcoreInstall(["minimap2/index", "minimap2/align"])
    nfcoreLink(launchDir, "${baseDir}/modules")
}

when {
    ...
}

then {
    ...
}

cleanup {
    nfcoreUnlink("${baseDir}/modules")
    // optional - you can also delete the library
    nfcoreDeleteLibrary(launchDir)
}

```
