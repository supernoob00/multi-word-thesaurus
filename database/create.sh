#!/bin/bash

export PGPASSWORD='postgres1'
BASEDIR=$(dirname $0)
DATABASE=thesaurus
psql -U postgres -d $DATABASE -f "$BASEDIR/schema.sql" &&
psql -U postgres -d $DATABASE -f "$BASEDIR/words_table_data" &&
psql -U postgres -d $DATABASE -f "$BASEDIR/synonyms_table_data.sql"