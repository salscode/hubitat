definition(
    name: "ZigBee Association Tool",
    namespace: "salscode",
    author: "Sal Sodano",
    description: "Create direct associations from one ZigBee device to another.",
    category: "My Apps",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "")

preferences {
    page(name: "mainPage", title: "Associations", install: true, uninstall: true, submitOnChange: true) {
        section {
            app(name: "association", appName: "ZigBee Association", namespace: "salscode", title: "Create New Association", multiple: true)
            }
        section {
            paragraph "This tool allows you to create direct associations between multiple ZigBee devices. In order for it to create the association, the source device handler needs to have support for this tool added to it. The destination does not need anything added to it's handler, but does need to be a ZigBee device."
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
