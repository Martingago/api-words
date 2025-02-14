```bash
conda create --name scrap_web python=3.12 -y
```

```bash
source activate base
conda activate scrap_web
```

```bash
conda install anaconda::requests
conda install anaconda::beautifulsoup4
conda install conda-forge::fake-useragent
pip install cloudscraper
pip install py_eureka_client
```

Docker

```bash
docker build -t rae-scraper-service .
docker run -d -p 8000:8000 --name rae-scraper-service rae-scraper-service
```