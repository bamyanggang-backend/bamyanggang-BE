#!/bin/bash

# Start the application
echo "Starting the application..."
bash scripts/start.sh

# Deploy or run additional deployment scripts
echo "Running the deployment script..."
bash deploy.sh
