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
        command_to_run = "gpt index workspace"
        app, list_ids = self.send_message_with_content(command_to_run)
        response_to_search_for = "Workspace has been indexed"
        responds_correctly = self.waits_for_response(app, list_ids, response_to_search_for)
        if not responds_correctly:
            self.fail("App was asked to index workspace but did not respond in 200 seconds")

    @pytest.mark.integration
    def test_will_query_workspace(self):
        command_to_run = "gpt query workspace What colour is steve's car?"
        app, list_ids = self.send_message_with_content(command_to_run)
        response_to_search_for = "blue"
        responds_correctly = self.waits_for_response(app, list_ids, response_to_search_for)
        if not responds_correctly:
            self.fail("App was asked to index workspace but did not respond in 200 seconds")

    @pytest.mark.integration
    def test_will_index_to_personal_workspace_then_query_it(self):
        command_to_run = "gpt my_knowledge_space index channels random"
        app, list_ids = self.send_message_with_content(command_to_run)
        response_to_search_for = "users knowledge space"
        responds_correctly = self.waits_for_response(app, list_ids, response_to_search_for)
        if not responds_correctly:
            self.fail("App was asked to index workspace but did not respond in 200 seconds")
        new_command_to_run = "gpt my_knowledge_space query What fruit did astronauts discover on pluto?"
        response_to_search_for = "oranges"
        app, list_ids = self.send_message_with_content(new_command_to_run)
        responds_correctly = self.waits_for_response(app, list_ids, response_to_search_for)
        if not responds_correctly:
            self.fail("App was asked to index workspace but did not respond in 200 seconds")

    def send_message_with_content(self, command_to_run):
        app_runner = get_app()
        SocketModeHandler(app_runner, os.environ["SLACK_APP_TOKEN"]).connect()
        time.sleep(10)
        client = WebClient(token=os.environ["SLACK_TEST_BOT_TOKEN"], ssl=ssl_context)
        app = App(client=client)
        list_channels = app.client.conversations_list().get("channels")
        list_ids = filter_channels(list_channels, ["test-channel"])
        app.client.chat_postMessage(channel=list_ids[0], text=command_to_run)
        return app, list_ids

    def waits_for_response(self, app, list_ids, response_to_search_for, wait_time_in_decseconds=20):
        responds_correctly = False
        for i in range(wait_time_in_decseconds):
            print("Waiting for response after " + str(i * 10) + " seconds")
            time.sleep(10)
            result = app.client.conversations_history(channel=list_ids[0])
            messages = result["messages"]
            print(messages[0])
            if len(messages) != 0:
                if str(messages[0]["text"]).find(response_to_search_for) != -1:
                    responds_correctly = True
                    break
        return responds_correctly