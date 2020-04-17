package io.mrlokop.kotlin.utils.conventer.utils

import java.lang.NullPointerException

class ConverterScope {
    var packages = mutableMapOf<String, PackageContainer>()

    fun getPackage(pack: String): PackageContainer {
        return packages.get(pack) ?: throw NullPointerException("Package '$pack' not found")
    }

    fun getPackageOrCreate(pack: String): PackageContainer {
        if (packages.containsKey(pack)) return packages[pack]!! else {
            packages[pack] = PackageContainer();
            return packages[pack]!!
        }
    }
}