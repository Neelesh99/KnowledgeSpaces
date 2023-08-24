FROM python
WORKDIR /app
COPY LLMServer .
RUN ls
RUN pip install -r requirements.txt
EXPOSE 2323:2323
ENTRYPOINT ["python", "api_check.py"]