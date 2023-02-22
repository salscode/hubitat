definition(
    name: "ZigBee Binding Tool",
    namespace: "salscode",
    author: "Sal Sodano",
    description: "Create direct bindings between ZigBee devices.",
    category: "My Apps",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "")

preferences {
    page(name: "mainPage", title: "Bindings", install: true, uninstall: true, submitOnChange: true) {
        section {
            app(name: "binding", appName: "ZigBee Binding", namespace: "salscode", title: "Create New Binding", multiple: true)
            }
        section {
            paragraph "This tool allows you to create bindings between multiple ZigBee devices."
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.debug "there are ${childApps.size()} child smartapps"
    childApps.each {child ->
        log.debug "child app: ${child.label}"
    }
}
