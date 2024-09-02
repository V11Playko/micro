#!/usr/bin/env groovy

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }

    tools {
        jdk 'jdk-17.0.7'
        gradle 'gradle-8.2'
        dockerTool 'docker-latest'
    }

    environment {
        POM_VERSION = getVersion()
        JAR_NAME = getJarName()
        AWS_ECR_REGION = 'us-east-1'
        AWS_ECS_SERVICE = 'src--micro-test'
        AWS_ECS_TASK_DEFINITION = 'micro-task'
        AWS_ECS_COMPATIBILITY = 'FARGATE'
        AWS_ECS_NETWORK_MODE = 'awsvpc'
        AWS_ECS_CPU = '1024'
        AWS_ECS_MEMORY = '4096'
        AWS_ECS_CLUSTER = 'micro-cluster'
        AWS_ECS_TASK_DEFINITION_PATH = './ecs/task-definition.json'
        AWS_ECR_REPO = '98011/microcurriculo-demo'
        IMAGE_TAG = "1"
        AWS_ACCOUNT_ID = '471112963469'
        DOCKER_IMAGE = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_ECR_REGION}.amazonaws.com/${AWS_ECR_REPO}:${IMAGE_TAG}"
    }

    stages {
        stage('Build & Test') {
            steps {
                withGradle {
                    bat "gradlew.bat clean build"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Autenticarse en el repositorio ECR
                    bat "aws ecr get-login-password --region ${AWS_ECR_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_ECR_REGION}.amazonaws.com"

                    // Construir la imagen Docker usando el Dockerfile
                    bat "docker build -t ${DOCKER_IMAGE} ."

                    // Etiquetar la imagen con el nombre del repositorio ECR y el tag
                    bat "docker tag ${DOCKER_IMAGE} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_ECR_REGION}.amazonaws.com/${AWS_ECR_REPO}:${IMAGE_TAG}"
                }
            }
        }

        stage('Push Image to ECR') {
            steps {
                script {
                    // Subir la imagen al repositorio ECR
                    bat "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_ECR_REGION}.amazonaws.com/${AWS_ECR_REPO}:${IMAGE_TAG}"
                }
            }
        }

        stage('Deploy in ECS') {}
    }

    post {}
}