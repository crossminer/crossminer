# OSSMeter metrics Dashboard

The OSSMeter metrics dashboard shows all metrics collected by OSSMeter platform of the type `history`. Metrics can be filtered with:

* Project
* Data source
* Metric class
* Metric name
* Metric compute type (sample, cumulative, average)
* Topic


## Start Elasticsearch and Kibana services

Please follow this section in the dependencies dashboard.

## Start OSSMeter services

In order to collect the metrics with OSSMeter the best approach is to
use the docker compose which starts the full platform:

`docker-compose -f docker/ossmeter.yml up`

[Create a initial user](https://github.com/ossmeter/ossmeter/wiki/FAQ#adding-the-first-user-in-the-local-web-application) to access OSSMeter.

Add two projects using the web interface with the GitHub urls:

* https://github.com/grimoirelab/perceval
* https://github.com/grimoirelab/grimoire_elk

You will need around 4h to collect the initial version of all metrics.

To avoid this process, an all ready populated MongoDB database is included in:

`data/gelk-perceval-mongodb.tar.bz2`

To use it:

```
docker-compose -f docker/ossmeter.yml stop
sudo rm -rf ~/oss-data/*
sudo tar xfj data/gelk-perceval-mongodb.tar.bz2 -C ~/oss-data/ --strip-components=3
docker-compose -f docker/ossmeter.yml start oss-db
```

With mongodb populated with metrics data let's process the metrics so they
can be used in Kibana.


## Install

The data processing is done with GrimoireLab python platform.

A virtual env in Python is used to install the tools needed.

In Debian/Ubuntu you need to execute:

`sudo apt-get install python3-venv`

To create the python virtualenv and activate it:

```
mkdir ~/venvs
python3 -m venv ~/venvs/crossminer
source ~/venvs/crossminer/bin/activate
pip3 install grimoire-elk
```



Now the tool can be executed with:

```
./mongo2es.py -g -e http://bitergia:bitergia@localhost:9200 -i ossmeter --project perceval
./mongo2es.py -g -e http://bitergia:bitergia@localhost:9200 -i ossmeter --project GrimoireELK
```

And now the Kibana panel can be imported to show the data:

`kidash.py -e http://bitergia:bitergia@localhost:9200 --import panels/ossmeter-metrics.json`

The panel is ready at the URL:

http://localhost:5601/app/kibana#/dashboard/AV7RLRT1TD5aYQ-eX8nd

![](screenshot.png?raw=true)

There is a [draft reference manual](https://docs.google.com/document/d/1OJj6WNgAsR9UvWGOThIoyjucSMNH55B88qQ8e52NPVY/edit?usp=sharing) available for this dashboard.
