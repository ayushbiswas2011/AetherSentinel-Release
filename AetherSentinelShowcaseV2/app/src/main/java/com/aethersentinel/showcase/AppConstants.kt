package com.aethersentinel.showcase

const val GITHUB_REPO_URL = "https://github.com/ayushbiswas2011/OkAether-Release"

const val APP_SHARE_MESSAGE =
    "Just tested the OkAether SDK! It literally stops unnecessary background API retry storms " +
    "and slashes server bills by 40%. Check out the open-source release here: " +
    "https://github.com/ayushbiswas2011/OkAether-Release"

const val GRADLE_MAVEN_SNIPPET =
    "dependencies {\n    implementation(\"io.github.ayushbiswas2011:OkAether:1.0.0\")\n}"

const val GRADLE_MIRROR_SNIPPET =
    "apply from: 'https://raw.githubusercontent.com/ayushbiswas2011/OkAether-Release/main/init.gradle'"

const val INIT_SNIPPET =
    "class MyApp : Application() {\n" +
    "    override fun onCreate() {\n" +
    "        super.onCreate()\n" +
    "        OkAether.bootstrap(\n" +
    "            context = this,\n" +
    "            licenseKey = \"YOUR_LICENSE_KEY\"\n" +
    "        )\n" +
    "    }\n" +
    "}"
