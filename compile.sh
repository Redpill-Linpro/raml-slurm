#! /bin/sh
mkdir -p out/
java -agentlib:native-image-agent=config-output-dir=out/conf/ -jar app.jar build-input.txt out.log
cat out/conf/reflect-config.json | ./jq '. + [{"name": "java.lang.reflect.AccessibleObject","methods":[{"name":"canAccess"}]}]' | tee out/conf/reflect-config.json
native-image -cp app.jar -jar app.jar \
	     -H:+ReportExceptionStackTraces \
	     -H:ConfigurationFileDirectories=out/conf/ \
	     -H:Name=app -H:+ReportExceptionStackTraces \
	     -J-Dclojure.spec.skip.macros=true -J-Dclojure.compiler.direct-linking=true -J-Xmx4G \
	     --initialize-at-build-time --enable-http --enable-https --verbose --no-fallback --no-server \
	     --initialize-at-run-time=org.apache.jena.sparql.engine.http.HttpQuery,org.apache.jena.riot.web.HttpOp,com.github.jsonldjava.utils.JsonUtils,org.apache.jena.atlas.lib.RandomLib \
	     --rerun-class-initialization-at-runtime=javax.net.ssl.SSLContext \
	     --rerun-class-initialization-at-runtime=org.httpkit.client.SslContextFactory \
	     --report-unsupported-elements-at-runtime --native-image-info \
	     -H:+StaticExecutableWithDynamicLibC -H:CCompilerOption=-pipe \
	     -H:+AllowIncompleteClasspath --enable-url-protocols=http,https --enable-all-security-services --trace-object-instantiation=sun.security.provider.NativePRNG
chmod +x app
echo "Size of generated native-image `ls -sh app`"
