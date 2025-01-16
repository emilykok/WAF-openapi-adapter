# OpenAPI2Frank Adapter
This project will allow you to convert an OpenAPI file or url to a WeAreFrank! Adapter.

# Contents

- [Installation](#installation)
- [Sequence Diagram](#sequence-diagram)

# Installation
## Clone the OpenAPI Adapter
Clone this project into your preferred folder.

```
git clone https://github.com/wearefrank/openapi-frank-generator
```

You can now run the OpenAPI Adapter on your own machine.

The application has finished starting up when you see this message:
```bash
Started OpenapiFrankadapterApplication in 2.802 seconds (process running for 3.992)
```

You can now connect to the following address:

http://localhost:8080

## Online

An online instance of this project can be found at:

https://openapi-frank-generator.wearefrank.org/


# Sequence Diagram

```mermaid
sequenceDiagram
    participant App/Site 
    participant OpenapiFrankadapterApplication
    participant XMLGenerator
    participant AdapterRefs
    participant XSDGenerator
    participant AdapterExits
    participant Handlebars
    participant Templates
    participant PrettyPrinter

    App/Site->>OpenapiFrankadapterApplication: OpenAPI file & option (Receiver/Sender)
    OpenapiFrankadapterApplication->>XMLGenerator: File containing OpenAPI spec
    XMLGenerator->>AdapterRefs: OpenAPI spec & Operation
    AdapterRefs->>XSDGenerator: API References
    destroy XSDGenerator
    XSDGenerator->>AdapterRefs: XSD
    destroy AdapterRefs
    AdapterRefs->>XMLGenerator: XSD
    XMLGenerator->>AdapterExits: Operations (GET, POST etc.)
    destroy AdapterExits
    AdapterExits->>XMLGenerator: List of Response objects
    XMLGenerator->>Handlebars: Template as String
    destroy Handlebars
    Handlebars->>Templates: Compile<br/> Template
    destroy Templates
    Templates->>XMLGenerator: Compiled Template
    XMLGenerator->>PrettyPrinter: XML String
    destroy PrettyPrinter
    PrettyPrinter->>XMLGenerator: Pretty XML String
    destroy XMLGenerator
    XMLGenerator->>OpenapiFrankadapterApplication: XML
    OpenapiFrankadapterApplication->>App/Site: Zip file
    
```
