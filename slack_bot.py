import os
import ssl

import certifi
from gpt_index import GPTSimpleVectorIndex
from slack_bolt import App
from slack_bolt.adapter.socket_mode import SocketModeHandler
from slack_sdk import WebClient

from construct_index import IndexMaker

ssl_context = ssl.create_default_context(cafile=certifi.where())


def get_app():
    client = WebClient(token=os.environ["SLACK_BOT_TOKEN"], ssl=ssl_context)
    app = App(client=client)

    @app.message("gpt index workspace")
    def index_workspace(message, say):

        list_channels = app.client.conversations_list().get("channels")
        list_ids = []
        for channel in list_channels:
            if channel["is_member"]:
                list_ids.append(channel["id"])
        index = IndexMaker.get_index_from_slack(list_ids)
        index.save_to_disk("workspace_index.json")
        say("Workspace has been indexed: use query command to query it")

    @app.message("gpt query")
    def gpt_query(message, say):
        split_on_query = str(message["text"]).split("gpt query")
        actual_query = split_on_query[1]
        index = GPTSimpleVectorIndex.load_from_disk('workspace_index.json')
        response = index.query(actual_query)
        say(response.response)
    return app
