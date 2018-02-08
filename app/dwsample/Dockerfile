FROM fabric8/java-centos-openjdk8-jdk

#ADD jmx_exporter_config.yml /opt/agent-bond/
COPY target/dwsample-1.0-SNAPSHOT.jar /deployments

EXPOSE 8080

CMD [ "/deployments/run-java.sh", "server" ]
