# Using nft-stagenfcore

This plugin requires that nf-core tools is installed in order to install modules. You can install it with one of the following:

```bash
conda install -c bioconda nf-core
```

or

```bash
pip install nf-core
```

## Initialise an nf-core library

In a setup block, run the `nfcoreSetup()` function to initialise an nf-core library. This function takes a string as an argument that is a path pointing to the location to set up the library. It is recommended to use `launchDir` as this will initialise a test-specific library.

```groovy
setup {
    nfcoreSetup("${launchDir}/library")
}
```

## Install modules

Use the `nfcoreInstall()` function to install nf-core modules. This function takes the path to the library and a list of strings, each with an nf-core module name in `tool/subtool` format.

```groovy
setup {
    nfcoreSetup("${launchDir}/library")
    nfcoreInstall("${launchDir}/library", ["minimap2/index", "minimap2/align"])
}
```

## Link the library to your modules directory

Use the `nfcoreLink()` function to link the library to your module library. This function takes two arguments, the path to a temporary library, and the location where the modules in the library should be temporarily linked (e.g. `${baseDir}/modules/nf-core`):

```groovy
setup {
    nfcoreSetup("${launchDir}/library")
    nfcoreInstall("${launchDir}/library", ["minimap2/index", "minimap2/align"])
    nfcoreLink("${launchDir}/library", "${baseDir}/modules/nf-core")
}
```

This creates a symlink to the modules directory of your temporary library at `${baseDir}/modules/nf-core`. Using this location, you can refer to the nf-core modules as if they were installed as normal in your tests.

## Clean up after tests are finished

To unlink the temporary library after the test has completed, use the `nfcoreUnlink()` function. It takes the path to the symlink as an input.

You can also use the `nfcoreDeleteLibrary()` function to remove the temporary library, if desired.

```groovy

setup {
    nfcoreSetup("${launchDir}/library")
    nfcoreInstall("${launchDir}/library", ["minimap2/index", "minimap2/align"])
    nfcoreLink("${launchDir}/library", "${baseDir}/modules/nf-core")

    run("MINIMAP2_INDEX") {
        script "${baseDir}/modules/nf-core/minimap2/index/main.nf
        ...
    }
}

when {
    ...
}

then {
    ...
}

cleanup {
    nfcoreUnlink("${baseDir}/modules/nf-core")
    // optional - you can also delete the library
    nfcoreDeleteLibrary("${launchDir}/library")
}
```
