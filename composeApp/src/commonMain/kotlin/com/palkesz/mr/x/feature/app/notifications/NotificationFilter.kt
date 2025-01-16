package com.palkesz.mr.x.feature.app.notifications

import com.palkesz.mr.x.proto.LocalNotificationType

data class NotificationFilter(val gameId: String?, val type: LocalNotificationType)
