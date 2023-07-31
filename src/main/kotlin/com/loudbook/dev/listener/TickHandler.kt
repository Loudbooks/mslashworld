package com.loudbook.dev.listener

import com.loudbook.dev.TimerManager
import net.minestom.server.event.EventListener
import net.minestom.server.event.server.ServerTickMonitorEvent

class TickHandler(private val timerManager: TimerManager) : EventListener<ServerTickMonitorEvent> {
    private var ticks: Int = 0
    override fun eventType(): Class<ServerTickMonitorEvent> {
        return ServerTickMonitorEvent::class.java
    }

    override fun run(event: ServerTickMonitorEvent): EventListener.Result {
        ticks++
        if (ticks % 20 != 0) return EventListener.Result.SUCCESS

        return EventListener.Result.SUCCESS
    }
}