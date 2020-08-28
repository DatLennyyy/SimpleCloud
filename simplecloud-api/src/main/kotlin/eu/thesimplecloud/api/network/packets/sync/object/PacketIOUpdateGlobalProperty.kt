/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.network.packets.sync.`object`

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOUpdateGlobalProperty() : JsonPacket() {

    constructor(name: String, property: IProperty<*>) : this() {
        this.jsonLib.append("property", property)
                .append("name", name)

    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val name = this.jsonLib.getString("name") ?: return contentException("name")
        val property = this.jsonLib.getObject("property", Property::class.java)
                ?: return contentException("property")
        CloudAPI.instance.getGlobalPropertyHolder().updatePropertyFromPacket(name, property)
        return unit()
    }
}