import requests
import urllib3
import logging

# Configurar logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

class RetrySession(requests.Session):
    def __init__(self, retries=3, backoff_factor=0.3, status_forcelist=(500, 502, 504)):
        super().__init__()
        retry = urllib3.util.Retry(
            total=retries,
            read=retries,
            connect=retries,
            backoff_factor=backoff_factor,
            status_forcelist=status_forcelist,
        )
        adapter = requests.adapters.HTTPAdapter(
            pool_connections=100, 
            pool_maxsize=100, 
            max_retries=retry
        )
        self.mount('http://', adapter)
        self.mount('https://', adapter)
    
    def get(self, *args, **kwargs):
        logger.debug(f"Making request to: {args[0]}")
        logger.debug(f"Headers: {kwargs.get('headers', {})}")
        response = super().get(*args, **kwargs)
        logger.debug(f"Response status: {response.status_code}")
        logger.debug(f"Response headers: {dict(response.headers)}")
        return response