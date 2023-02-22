# ZigBee Binding Tool

Only tested with Inovelli Blue Switches and a handful of ZigBee bulbs.

## Install

Make sure to install both the ZigBee Binding Tool and the ZigBee Binding apps.

## Functionality

After adding the ZigBee Binding Tool app, open it up and click Create New Binding.

This page let's you do two pieces of functionality within one page.

### 1. Device to Device Binding (Bidirectional)

All of the devices selected in the first selection box will be bound to each other bidirectionally.

Example:
* You select 3 Blue switches (let's call them switch A, B, and C) and specify 0x02 for the source endpoint and 0x01 for the destination endpoint.
* The app will bind the following:
  * `A:0x02` to `B:0x01`
  * `B:0x02` to `A:0x01`
  * `A:0x02` to `C:0x01`
  * `C:0x02` to `A:0x01`
  * `B:0x02` to `C:0x01`
  * `C:0x02` to `B:0x01`

### 2. Device to Slave Binding (Unidirectional)

The device selected in the first selection box will be bound to the devices selected in the slave devices list, unidirectionally.

Example:
* You select 1 Blue switch and specify 0x02 for the source endpoint.
* Under slave devices, you select 2 bulbs with slave endpoint 0x0B.
* The app will bind the following:
  * `Switch:0x02` to `Bulb1:0x0B`
  * `Switch:0x02` to `Bulb2:0x0B`
