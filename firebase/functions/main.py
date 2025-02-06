from firebase_admin import initialize_app, firestore, messaging, auth
from firebase_functions.firestore_fn import (
    on_document_created,
    on_document_updated,
    Event,
    Change,
    DocumentSnapshot,
)
from firebase_functions import logger, https_fn
import google.cloud.firestore
from typing import Any

EN_STRINGS = {
    "new_question_notification_title": "New question by %s",
    "question_answered_notification_title": "Question answered by %s",
    "question_passed_notification_title": "Question passed by %s",
    "question_specified_notification_title": "Question specified by %s",
    "question_guessed_notification_title": "Question guessed by %s",
    "question_missed_notification_title": "Question missed by %s",
    "host_answer_rejected_notification_title": "%s's answer rejected, question is ready to answer",
    "game_over_notification_title": "Game won by %s",
    "new_barkochba_question_notification_title": "New barkochba question by %s",
    "barkochba_question_answered_notification_title": "Barkochba question answered by %s",
    "yes_label": "Yes",
    "no_label": "No",
}

HU_STRINGS = {
    "new_question_notification_title": "%s új kérdést tett fel",
    "question_answered_notification_title": "%s megválaszolta a kérdést",
    "question_passed_notification_title": "%s passzolta a kérdést",
    "question_specified_notification_title": "%s szűkített a kérdésén",
    "question_guessed_notification_title": "%s kitalálta a kérdést",
    "question_missed_notification_title": "%s elhibázta a kérdést",
    "host_answer_rejected_notification_title": "%s válaszát visszautasították, most te jössz",
    "game_over_notification_title": "%s megnyerte a játékot",
    "new_barkochba_question_notification_title": "%s új barkochba-kérdést tett fel",
    "barkochba_question_answered_notification_title": "%s megválaszolta a barkochba-kérdést",
    "yes_label": "Igen",
    "no_label": "Nem",
}

STRINGS = {
    "en": EN_STRINGS,
    "hu": HU_STRINGS,
}

initialize_app()

# HTTP functions


@https_fn.on_call(enforce_app_check=False)  # TODO enforce app check
def deleteAccount(req: https_fn.CallableRequest) -> Any:
    firestore_client: google.cloud.firestore.Client = firestore.client()
    playerCollection = firestore_client.collection("players")
    playerCollection.document(req.auth.uid).update({"name": "N/A"})
    auth.delete_user(req.auth.uid)
    return {}


# Question notification triggers


@on_document_created(document="questions/{questionId}")
def onQuestionCreated(event: Event[DocumentSnapshot]) -> None:
    firestore_client: google.cloud.firestore.Client = firestore.client()
    data = event.data.to_dict()
    tokenCollection = firestore_client.collection("tokens")
    gameCollection = firestore_client.collection("games")
    playerCollection = firestore_client.collection("players")
    recipients: list[str] = []
    notificationTitleRes: str = ""
    notificationBody: str = ""
    username = playerCollection.document(data["userId"]).get().to_dict()["name"]
    game = gameCollection.document(data["gameId"]).get().to_dict()
    hostId = gameCollection.document(data["gameId"]).get().to_dict()["hostId"]
    recipients.append(hostId)
    if data["status"] == "WAITING_FOR_HOST":
        notificationTitleRes = "new_question_notification_title"
        notificationBody = capfirst(f"{data["text"]}?")
    else:
        notificationTitleRes = "game_over_notification_title"
        notificationBody = (
            f"{capfirst(data["expectedFirstName"])} {capfirst(data["expectedLastName"] if data["expectedLastName"] is not None else "")}"
        ).strip()
        players: list[str] = game.get("players", [])
        recipients.append(players)
    if recipients.count == 0:
        return
    tokens = tokenCollection.where("userId", "in", recipients).get()
    messages = []
    logger.info(f"Recipients: {recipients}")
    for token in tokens:
        tokenData = token.to_dict()
        locale = tokenData["locale"]
        strings = STRINGS.get(locale, STRINGS["en"])
        notificationTitle = strings[notificationTitleRes] % username
        notification = messaging.Notification(
            title=notificationTitle,
            body=notificationBody,
        )
        payloadData = {
            "type": "QUESTION",
            "status": data["status"],
            "questionId": data["id"],
            "gameId": data["gameId"],
            "userId": data["userId"],
        }
        message = messaging.Message(
            token=tokenData["token"],
            data=payloadData,
            notification=notification,
            apns=messaging.APNSConfig(
                payload=messaging.APNSPayload(
                    aps=messaging.Aps(sound="default", mutable_content=True)
                )
            ),
        )
        logger.info(f"Message token: {tokenData["token"]}")
        messages.append(message)
    messaging.send_each(messages)


@on_document_updated(document="questions/{questionId}")
def onQuestionUpdated(event: Event[Change[DocumentSnapshot]]) -> None:
    firestore_client: google.cloud.firestore.Client = firestore.client()
    before = event.data.before.to_dict()
    after = event.data.after.to_dict()
    tokenCollection = firestore_client.collection("tokens")
    gameCollection = firestore_client.collection("games")
    playerCollection = firestore_client.collection("players")
    recipients: list[str] = []
    notificationTitleRes: str = ""
    notificationTitleParameter: str = ""
    notificationBody: str = ""
    if (
        before["status"] == "WAITING_FOR_HOST"
        and after["status"] == "WAITING_FOR_OWNER"
    ):
        hostId = after["hostAnswer"]["userId"]
        notificationTitleParameter = (
            playerCollection.document(hostId).get().to_dict()["name"]
        )
        notificationTitleRes = "question_answered_notification_title"
        notificationBody = capfirst(
            f"{after["text"]}? {capfirst(after["hostAnswer"]["firstName"])} {capfirst(after["hostAnswer"]["lastName"] if after["hostAnswer"]["lastName"] is not None else "")}"
        ).strip()
        recipients.append(after["userId"])
    elif (
        before["status"] == "WAITING_FOR_HOST"
        and after["status"] == "WAITING_FOR_PLAYERS"
    ):
        game = gameCollection.document(after["gameId"]).get().to_dict()
        notificationTitleParameter = (
            playerCollection.document(game["hostId"]).get().to_dict()["name"]
        )
        notificationTitleRes = "question_passed_notification_title"
        notificationBody = capfirst(f"{after["text"]}?")
        players: list[str] = game.get("players", [])
        recipients.append(players)
    elif (
        before["status"] == "WAITING_FOR_OWNER"
        and after["status"] == "WAITING_FOR_HOST"
    ):
        hostId = after["hostAnswer"]["userId"]
        notificationTitleParameter = (
            playerCollection.document(after["userId"]).get().to_dict()["name"]
        )
        notificationTitleRes = "question_specified_notification_title"
        notificationBody = capfirst(f"{after["text"]}?")
        recipients.append(hostId)
    elif (
        before["status"] == "WAITING_FOR_OWNER"
        and after["status"] == "WAITING_FOR_PLAYERS"
    ):
        game = gameCollection.document(after["gameId"]).get().to_dict()
        notificationTitleParameter = (
            playerCollection.document(game["hostId"]).get().to_dict()["name"]
        )
        notificationTitleRes = "host_answer_rejected_notification_title"
        notificationBody = capfirst(f"{after["text"]}?")
        players: list[str] = game.get("players", [])
        recipients.append(players)
    elif (
        before["status"] == "WAITING_FOR_PLAYERS"
        and after["status"] == "GUESSED_BY_PLAYER"
    ):
        game = gameCollection.document(after["gameId"]).get().to_dict()
        playerId = after["playerAnswer"]["userId"]
        notificationTitleParameter = (
            playerCollection.document(playerId).get().to_dict()["name"]
        )
        notificationTitleRes = "question_guessed_notification_title"
        notificationBody = capfirst(
            f"{after["text"]}? {capfirst(after["playerAnswer"]["firstName"])} {capfirst(after["playerAnswer"]["lastName"] if after["playerAnswer"]["lastName"] is not None else "")}"
        ).strip()
        recipients.append(game["hostId"])
        recipients.append(after["userId"])
    elif (
        before["status"] == "WAITING_FOR_PLAYERS"
        and after["status"] == "MISSED_BY_PLAYER"
    ):
        game = gameCollection.document(after["gameId"]).get().to_dict()
        playerId = after["playerAnswer"]["userId"]
        notificationTitleParameter = (
            playerCollection.document(playerId).get().to_dict()["name"]
        )
        notificationTitleRes = "question_missed_notification_title"
        notificationBody = capfirst(
            f"{after["text"]}? {capfirst(after["playerAnswer"]["firstName"])} {capfirst(after["playerAnswer"]["lastName"] if after["playerAnswer"]["lastName"] is not None else "")}"
        ).strip()
        recipients.append(game["hostId"])
        recipients.append(after["userId"])
    elif (
        before["status"] == "WAITING_FOR_HOST" and after["status"] == "GUESSED_BY_HOST"
    ):
        hostId = after["hostAnswer"]["userId"]
        notificationTitleParameter = (
            playerCollection.document(hostId).get().to_dict()["name"]
        )
        notificationTitleRes = "question_guessed_notification_title"
        notificationBody = capfirst(
            f"{after["text"]}? {capfirst(after["hostAnswer"]["firstName"])} {capfirst(after["hostAnswer"]["lastName"] if after["hostAnswer"]["lastName"] is not None else "")}"
        ).strip()
        recipients.append(after["userId"])
    if recipients.count == 0:
        return
    tokens = tokenCollection.where("userId", "in", recipients).get()
    messages = []
    logger.info(f"Recipients: {recipients}")
    for token in tokens:
        tokenData = token.to_dict()
        locale = tokenData["locale"]
        strings = STRINGS.get(locale, STRINGS["en"])
        notificationTitle = strings[notificationTitleRes] % notificationTitleParameter
        notification = messaging.Notification(
            title=notificationTitle,
            body=notificationBody,
        )
        payloadData = {
            "type": "QUESTION",
            "status": after["status"],
            "questionId": after["id"],
            "gameId": after["gameId"],
            "userId": after["userId"],
        }
        message = messaging.Message(
            token=tokenData["token"],
            data=payloadData,
            notification=notification,
            apns=messaging.APNSConfig(
                payload=messaging.APNSPayload(
                    aps=messaging.Aps(sound="default", mutable_content=True)
                )
            ),
        )
        logger.info(f"Message token: {tokenData["token"]}")
        messages.append(message)
    messaging.send_each(messages)


# Barkochba notification triggers


@on_document_updated(document="barkochba/{questionId}")
def onBarkochbaQuestionUpdated(event: Event[Change[DocumentSnapshot]]) -> None:
    firestore_client: google.cloud.firestore.Client = firestore.client()
    after = event.data.after.to_dict()
    tokenCollection = firestore_client.collection("tokens")
    gameCollection = firestore_client.collection("games")
    playerCollection = firestore_client.collection("players")
    recipients: list[str] = []
    notificationTitleRes: str = ""
    notificationTitleParameter: str = ""
    notificationBody: str = ""
    notificationBodyRes: str = ""
    notificationBodyParameter: str = ""
    game = gameCollection.document(after["gameId"]).get().to_dict()
    hostId = game["hostId"]
    if after["status"] == "ASKED":
        notificationTitleRes = "new_barkochba_question_notification_title"
        notificationTitleParameter = (
            playerCollection.document(after["userId"]).get().to_dict()["name"]
        )
        notificationBody = capfirst(f"{after["text"]}?")
        recipients.append(hostId)
    else:
        notificationTitleRes = "barkochba_question_answered_notification_title"
        notificationTitleParameter = (
            playerCollection.document(hostId).get().to_dict()["name"]
        )
        notificationBodyParameter = f"{capfirst(after["text"])}?"
        notificationBodyRes = "yes_label" if after["answer"] == "YES" else "no_label"
        players: list[str] = game.get("players", [])
        recipients.append(players)
    if recipients.count == 0:
        return
    tokens = tokenCollection.where("userId", "in", recipients).get()
    messages = []
    logger.info(f"Recipients: {recipients}")
    for token in tokens:
        tokenData = token.to_dict()
        locale = tokenData["locale"]
        strings = STRINGS.get(locale, STRINGS["en"])
        notificationTitle = strings[notificationTitleRes] % notificationTitleParameter
        localizedNotificationBody = (
            notificationBody
            if notificationBody != ""
            else f"{notificationBodyParameter} {strings[notificationBodyRes]}"
        )
        notification = messaging.Notification(
            title=notificationTitle,
            body=localizedNotificationBody,
        )
        payloadData = {
            "type": "BARKOCHBA",
            "status": after["status"],
            "questionId": after["id"],
            "gameId": after["gameId"],
            "userId": after["userId"],
        }
        message = messaging.Message(
            token=tokenData["token"],
            data=payloadData,
            notification=notification,
            apns=messaging.APNSConfig(
                payload=messaging.APNSPayload(
                    aps=messaging.Aps(sound="default", mutable_content=True)
                )
            ),
        )
        logger.info(f"Message token: {tokenData["token"]}")
        messages.append(message)
    messaging.send_each(messages)


def capfirst(s):
    return s[:1].upper() + s[1:]
