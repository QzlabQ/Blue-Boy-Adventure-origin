plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

application {
    // 默认 ./gradlew run 启动游戏
    mainClass.set("main.Main")
    applicationName = "BlueBoyAdventure"
}

// 定义启动编辑器的任务
tasks.register<JavaExec>("runEditor") {
    group = "application"
    description = "启动地图编辑器"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("maptool.MapEditor")

    // 设置工作目录为项目根目录，确保能读取到 config.txt 和 src/ 目录
    workingDir = layout.projectDirectory.asFile

    jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dsun.java2d.uiScale=1.5")
}

// 直接启动游戏的 run 任务，设置工作目录和 JVM 参数
tasks.named<JavaExec>("run") {
    workingDir = layout.projectDirectory.asFile
    jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dsun.java2d.uiScale=2")
}

// 定义生成精简 JRE 的任务
tasks.register<Exec>("createRuntime") {
    group = "distribution"
    description = "生成精简版 JRE (Runtime Image)"

    // 1. 确定 jlink 可执行文件的路径 (从当前 Gradle 配置的 Java 工具链中获取)
    val jdkHome = javaToolchains.launcherFor(java.toolchain).get().metadata.installationPath.asFile
    val jlinkPath = jdkHome.resolve("bin/jlink").absolutePath

    // 2. 定义输出目录 (build/runtime)
    val outputDir = layout.buildDirectory.dir("runtime").get().asFile

    // 3. 执行前先清理旧目录，否则 jlink 会报错
    doFirst {
        println("正在清理旧的 runtime 目录...")
        delete(outputDir)
        println("正在使用 JDK: $jdkHome 生成精简 JRE...")
    }

    // 4. 配置 jlink 命令
    commandLine(
        jlinkPath,
        // 添加需要的模块
        // java.desktop: 包含 Swing, AWT, Font 等 GUI 库
        // java.logging: 日志模块
        // jdk.unsupported: 包含 sun.misc.Unsafe (很多高性能库如 Netty 或一些工具库需要)
        "--add-modules", "java.desktop",

        // 优化选项
        "--strip-debug",        // 移除调试信息，减小体积
        "--no-man-pages",       // 不包含 man 手册
        "--no-header-files",    // 不包含 C 头文件
        "--compress", "zip-6",      // 压缩级别

        // 输出路径
        "--output", outputDir
    )

    doLast {
        println("精简版 JRE 已生成: ${outputDir.absolutePath}")
    }
}

// 1. 构建游戏本体 Jar
tasks.register<Jar>("gameJar") {
    group = "build"
    description = "生成游戏运行 Jar"
    // 指定输出文件名
    archiveFileName.set("BlueBoyAdventure.jar")
    // 设置清单文件，指定入口类
    manifest {
        attributes["Main-Class"] = "main.Main"
    }
    // 包含编译后的源码和资源
    from(sourceSets.main.get().output)
}

// 2. 构建地图编辑器 Jar
tasks.register<Jar>("editorJar") {
    group = "build"
    description = "生成地图编辑器 Jar"
    archiveFileName.set("MapEditor.jar")
    manifest {
        attributes["Main-Class"] = "maptool.MapEditor"
    }
    from(sourceSets.main.get().output)
}