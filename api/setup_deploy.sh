mvn package;
cp target/Platform-1.0-SNAPSHOT-jar-with-dependencies.jar deploy/
git add -A;
git commit -m "Deploy new version";
git push origin master;

