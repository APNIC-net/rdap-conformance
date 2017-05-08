
echo "Launching with arguments: $@"

exec java "$\@" -jar @project.artifactId@-@project.version@.@project.packaging@
