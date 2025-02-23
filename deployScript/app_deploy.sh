#!/bin/bash
APPLICATION_HOME="/app/playnomm_wallet"
APPLICATION_START="/app/playnomm_wallet/bin/nomm_run.sh start"
APPLICATION_STATUS="/app/playnomm_wallet/bin/nomm_run.sh status"
APPLICATION_STOP="/app/playnomm_wallet/bin/nomm_run.sh stop"

cd $APPLICATION_HOME
echo "============ Deploy by Aws CodeDeploy Timestamp: `date`">>deploy_history.log
sh $APPLICATION_STOP>>deploy_history.log
sh $APPLICATION_START>>deploy_history.log
echo "===========================================================">>deploy_history.log