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
    nfcoreLink(launchDir, baseDir)
}
```

## Clean up after tests are finished

To clean up the library after tests are finished and remove any symlinks, use the `nfcoreCleanup()` function. It takes as arguments the path to your temporary library, and the path to your modules directory.

```groovy

setup {
    nfcoreSetup(launchDir)
    nfcoreInstall(["minimap2/index", "minimap2/align"])
    nfcoreLink(launchDir, baseDir)
}

when {
    ...
}

then {
    ...
}

cleanup {
    nfcoreCleanup(launchDir, baseDir)
}

```
