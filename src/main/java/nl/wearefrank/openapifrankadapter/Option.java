package nl.wearefrank.openapifrankadapter;

// Enum with the generator options, such as receiver or sender//
public enum Option {
    RECEIVER("/templates/receiverTemplate.hbs"),
    SENDER("/templates/senderTemplate.hbs"),
    XSD("");

    private final String templateName;

    Option(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

}
