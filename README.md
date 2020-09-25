# slack-command-bot project

This application is a Slack bot that respond to slash commands (/piglatin) which reads the input text following the command and translate it in Pig Latin Format and respond back with the translation.

The application uses Kafka to store both the input and the output(reply) to a kafka topic ("slack")

The application listens to command request url "http://<host-name>:8080/slash"

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `slack-command-bot-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/slack-command-bot-1.0-SNAPSHOT-runner.jar`.

## Building and Running on Openshift

### Build your Kafka cluster

First you need to have a kafka cluster running, I suggest looking to Strimzi operator for installing your kafka cluster on openshift.

For Openshift 4 and Above:
From Operator Hub, search for AMQ-Streams Operator and install.
Once installed create a kafka cluster, I called mine `cluster` and I created it in project called `kafka`

### Application configuration

The application has 2 configuration resources:

1. A configmap specifying the kafka broker, you may need to change the value based on your kafka cluster host, this configmap wil be translated to an environment variable as follow:
`KAFKA_BOOTSTRAP_SERVERS="cluster-kafka-bootstrap.kafka.svc:9092"`
Run the following command to create the configmap piggybee-env
```
oc create configmap --from-literal=KAFKA_BOOTSTRAP_SERVERS="cluster-kafka-bootstrap.kafka.svc:9092" piggybee-env```

2. A secret containing slack app connection configuration which are:
NOTE: This is not used in this basic functionality but it is recommended to check against those value for any incoming request to make sure this is generated from Slack to your app ant to respond with the token as a bearer header
* Bot token: `SLACK_BOT_TOKEN="<xoxb-token>"`
* Signing secret: `SLACK_SIGNING_SECRET="<signing-secret"`
Run the following command to create the secret
```
oc create secret generic --from-literal=SLACK_BOT_TOKEN=<xoxb-token> --from-literal=SLACK_SIGNING_SECRET=<signing-secret> piggybee-secret
```

For the application you can use the following command:

```
oc new-app java~https://github.com/AlyIbrahim/slack-command-bot.git --name=piggybee
```

Then you need to add environment variable from the configmap created earlier:
```
oc set env dc/piggybee --from configmap/piggybee-env
```

Now you just need to expose a route to add it in your Slack Configuration for Command Request:
```
oc expose svc piggybee
```
Use the HOST/PORT value as your value for http://<host:port>/slash, you can get the value using the following command
```
oc get route -l app=piggybee
```

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/slack-command-bot-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
