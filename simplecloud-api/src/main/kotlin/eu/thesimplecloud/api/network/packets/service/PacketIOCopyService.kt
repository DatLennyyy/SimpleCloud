/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.api.network.packets.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * Created by IntelliJ IDEA.
 * Date: 09.06.2020
 * Time: 14:21
 * @author Frederick Baier
 */
class PacketIOCopyService() : JsonPacket() {

    constructor(service: ICloudService, path: String) : this() {
        this.jsonLib.append("service", service.getName()).append("path", path)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val serviceName = this.jsonLib.getString("service") ?: return contentException("service")
        val path = this.jsonLib.getString("path") ?: return contentException("path")
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        service ?: return failure(NoSuchServiceException("Service is not registered"))
        return service.copy(path)
    }
}