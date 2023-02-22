/**
 *  ZigBee Binding
 *  Author: Sal Sodano (salscode)
 *  Date: 2023-02-20
 *
 */

definition(
    name: "ZigBee Binding",
    namespace: "salscode",
    author: "Sal Sodano",
    description: "Create direct bindings between ZigBee devices.",
    category: "My Apps",
    parent: "salscode:ZigBee Binding Tool",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "")


preferences {
    page name: "mainPage", title: "Bind ZigBee Devices", install: false, uninstall: true, nextPage: "namePage"
    page name: "namePage", title: "Bind ZigBee Devices", install: true, uninstall: true
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def uninstalled() {
    bindEnabled = false
    mainRun()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unschedule()
    initialize()
}

def initialize() {
    if (!overrideLabel) {
        app.updateLabel(defaultLabel())
    }
    
    mainRun()
}

def mainPage() {
    dynamicPage(name: "mainPage") {
        bindInputs()
    }
}

def namePage() {
    if (!overrideLabel) {
        def l = defaultLabel()
        log.debug "Default Label: $l"
        app.updateLabel(l)
    }

    dynamicPage(name: "namePage") {
        if (overrideLabel) {
            section("Binding Name") {
                label title: "Enter custom name", defaultValue: app.label, required: false
            }
        } else {
            section("Binding Name") {
                paragraph app.label
            }
        }
        section {
            input "overrideLabel", "bool", title: "Edit binding name", defaultValue: "false", required: "false", submitOnChange: true
        }
    }
}

def defaultLabel() {
    def bindingLabel
    bindingLabel = deviceList[0].displayName + " Binding"
    return bindingLabel
}

def bindInputs() {
    section("Device(s)") {
        input "deviceList", "capability.configuration", title: "Bind these devices to each other", multiple: true, required: true, submitOnChange: true
    }

    section("Endpoints") {
        input "sourceEndpoint", "string", title: "Source endpoint (0x__)", defaultValue: "0x02", required: true
        if (deviceList != null && deviceList.size() > 1) {
            input "destinationEndpoint", "string", title: "Destination endpoint (0x__)", defaultValue: "0x01", required: true
        }
    }

    section("Slave Device(s)") {
        input "slaveList", "capability.configuration", title: "and optionally one-way bind to these devieces...", multiple: true, required: false, submitOnChange: true
        if (slaveList != null && slaveList.size() > 0) {
            input "slaveEndpoint", "string", title: "Slave endpoint (0x__)", defaultValue: "0x0B", required: true
        }
    }
    
    section("Bindings") {
        input "bindPower", "bool", title: "Bind power", defaultValue: true
        input "bindLevel", "bool", title: "Bind level", defaultValue: true
        input "bindColor", "bool", title: "Bind color", defaultValue: true
    }
    section("Advanced") {
        input(name:"bindEnabled", type:"bool", title: "Bind? Enable for bind, disable for unbind.",
              description: "Bind? Enable for bind, disable for unbind.", defaultValue: true,
              required: true, displayDuringSetup: true)
        
        input(name:"logDebug", type:"bool", title: "Log debug information?",
              description: "Logs data for debugging.", defaultValue: true,
              required: true, displayDuringSetup: true)
    }
}

def logDebug(text){
    if(settings.logDebug){
        log.debug(text)
    }
}

def bindCommand(action, sourceDevice, destinationDevice, sourceEndpoint, destinationEndpoint, cluster) {
    return "zdo ${action} 0x${sourceDevice.deviceNetworkId} ${sourceEndpoint} ${destinationEndpoint} ${cluster} {${sourceDevice.zigbeeId}} {${destinationDevice.zigbeeId}}"
}

def allBindCommands(action, sourceDevice, destinationDevice, sourceEndpoint, destinationEndpoint, clusters) {
    List<String> commands = []
    for (cluster in clusters) {
        commands << bindCommand(bindAction, sourceDevice, destinationDevice, sourceEndpoint, destinationEndpoint, cluster)
        commands << "delay 200"
    }
    return commands
}

def mainRun() {
    List<String> commands = []
    List<String> clusters = []

    if (bindPower) {
        clusters << "0x0006"
    }

    if (bindLevel) {
        clusters << "0x0008"
    }

    if (bindColor) {
        clusters << "0x0300"
    }

    if (bindEnabled) {
        bindAction = 'bind'
    } else {
        bindAction = 'unbind'
    }

    for (sourceDevice in deviceList) {
        List<String> deviceCommands = []
        for (destinationDevice in deviceList) {
            if (sourceDevice.zigbeeId == destinationDevice.zigbeeId) {
                continue;
            }

            deviceCommands.addAll(allBindCommands(bindAction, sourceDevice, destinationDevice, sourceEndpoint, destinationEndpoint, clusters))
        }

        for (slaveDevice in slaveList) {
            if(sourceDevice.zigbeeId == slaveDevice.zigbeeId){
                log.warn "Failed ZigBee Bind: You cannot bind a device to itself."
                continue;
            }

            deviceCommands.addAll(allBindCommands(bindAction, sourceDevice, slaveDevice, sourceEndpoint, slaveEndpoint, clusters))
        }

        logDebug("Sending ${deviceCommands}")
        sourceDevice.bind(deviceCommands);
    }
}
