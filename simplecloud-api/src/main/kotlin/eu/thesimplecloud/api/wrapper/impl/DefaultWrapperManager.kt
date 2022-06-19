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

package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdateExecutor
import eu.thesimplecloud.api.event.wrapper.WrapperUpdatedEvent
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperInfoUpdater
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

open class DefaultWrapperManager : AbstractCacheList<IWrapperInfoUpdater, IWrapperInfo>(), IWrapperManager {

    private val updateLifecycle = object : ICacheObjectUpdateExecutor<IWrapperInfoUpdater, IWrapperInfo> {

        override fun getCachedObjectByUpdateValue(value: IWrapperInfo): IWrapperInfo? {
            return getWrapperByName(value.getName())
        }

        override fun determineEventsToCall(updater: IWrapperInfoUpdater, cachedValue: IWrapperInfo?): List<IEvent> {
            val wrapperToUse = cachedValue ?: updater.getWrapperInfo()
            return listOf(WrapperUpdatedEvent(wrapperToUse))
        }

        override fun addNewValue(value: IWrapperInfo) {
            values.add(value)
        }

        override fun getIdentificationName(): String {
            return "wrapper-cache"
        }

    }

    override fun getUpdateExecutor(): ICacheObjectUpdateExecutor<IWrapperInfoUpdater, IWrapperInfo> {
        return this.updateLifecycle
    }

    override fun delete(value: IWrapperInfo, fromPacket: Boolean): ICommunicationPromise<Unit> {
        if (value.getServicesRunningOnThisWrapper().isNotEmpty())
            throw IllegalStateException("Cannot delete wrapper while services are still running on this wrapper")
        if (CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupsByWrapperName(value.getName()).isNotEmpty())
            throw IllegalStateException("Cannot delete wrapper while groups are only able to start on this wrapper")
        return super<AbstractCacheList>.delete(value, fromPacket)
    }


}