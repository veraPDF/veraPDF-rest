

setup_suite() {
  echo -e "Starting tests ... \n" >&3
  export DOCKER_ID=$(docker ps | grep "rest:" | awk '{print $1}')
}

teardown_suite() {
  echo -e "\nEnding tests ... \n" >&3

  # docker logs $DOCKER_ID >&3
  
  sleep 3 # sleep sec

  docker logs "$DOCKER_ID" > ./results/container.log 2>&1
  sleep 3 # sleep sec
}
