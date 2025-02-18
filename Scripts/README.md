```bash
conda create --name scrap_web python=3.12 -y
```

```bash
source activate base
conda activate scrap_web
```

```bash
conda install anaconda::requests anaconda::beautifulsoup4 conda-forge::fake-useragent conda-forge::fastapi
pip install cloudscraper py_eureka_client
```

Docker

```bash
docker-compose up
```
