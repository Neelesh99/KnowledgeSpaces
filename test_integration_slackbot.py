import os
import ssl
import time
import unittest

import certifi
import pytest
from slack_bolt import App
from slack_bolt.adapter.socket_mode import SocketModeHandler
from slack_sdk import WebClient

from slack_bot import get_app, filter_channels

ssl_context = ssl.create_default_context(cafile=certifi.where())


class SlackBothIntegrationTestCase(unittest.TestCase):

    @pytest.mark.integration
    def test_will_index_workspace(self):
        app_runner = get_app()
        SocketModeHandler(app_runner, os.environ["SLACK_APP_TOKEN"]).connect()
        time.sleep(10)

        client = WebClient(token=os.environ["SLACK_TEST_BOT_TOKEN"], ssl=ssl_context)
        app = App(client=client)

        list_channels = app.client.conversations_list().get("channels")
        list_ids = filter_channels(list_channels, ["test-channel"])
        app.client.chat_postMessage(channel=list_ids[0], text="gpt index workspace")
        time.sleep(2)
        start = time.time()
        for i in range(20):
            print("Waiting for response after " + str(i*10) + " seconds")
            time.sleep(10)
            result = app.client.conversations_history(channel=list_ids[0], limit=1, oldest=str(start))
            print(result)
            messages = result["messages"]
            if len(messages) != 0:
                if str(messages[0]["text"]).find("Workspace has been indexed") != -1:
                    return
        self.fail("App was asked to index workspace but did not respond in 200 seconds")

    @pytest.mark.integration
    def test_will_query_workspace(self):
        app_runner = get_app()
        SocketModeHandler(app_runner, os.environ["SLACK_APP_TOKEN"]).connect()
        time.sleep(10)

        client = WebClient(token=os.environ["SLACK_TEST_BOT_TOKEN"], ssl=ssl_context)
        app = App(client=client)

        list_channels = app.client.conversations_list().get("channels")
        list_ids = filter_channels(list_channels, ["test-channel"])
        app.client.chat_postMessage(channel=list_ids[0], text="gpt query workspace What colour is steve's car?")
        time.sleep(1)
        start = time.time()
        for i in range(20):
            print("Waiting for response after " + str(i * 10) + " seconds")
            time.sleep(10)
            result = app.client.conversations_history(channel=list_ids[0], limit=1, oldest=str(start))
            print(result)
            messages = result["messages"]
            if len(messages) != 0:
                if str(messages[0]["text"]).find("blue") != -1:
                    return
        self.fail("App was asked to index workspace but did not respond in 200 seconds")