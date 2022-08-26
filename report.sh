#!/bin/bash

MAX_PRS=200
MAX_AGE="12"

DATE_EXPIRED=`date -d "-$MAX_AGE month" +%Y-%m-%d`

PR_COUNT=`gh api -X GET search/issues -f q="repo:keycloak/keycloak is:pr is:open" -f per_page=1 -q .total_count`
PR_EXPIRED_COUNT=`gh api -X GET search/issues -f q="repo:keycloak/keycloak is:pr is:open created:<$DATE_EXPIRED" -f per_page=1 -q .total_count`

echo "# Keycloak Community Dashboard"
echo ""

echo ""
echo "## Warnings"
echo ""

if [ "$PR_COUNT" -ge "$MAX_PRS" ]; then
  echo "* Too many open PRs";
fi

if [ "$PR_EXPIRED_COUNT" -gt "0" ]; then
  echo "* Some PRs have been around for too long"
fi

echo ""
echo "## GitHub Workflows"
echo ""
echo "* Nightly release: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak-rel/keycloak-rel/Release%20Nightly)"
echo "* Keycloak CI: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/Keycloak%20CI)"
echo "* Keycloak Operator CI: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/Keycloak%20Operator%20CI)"
echo "* CodeQL JS Adapter: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/CodeQL%20JS%20Adapter)"
echo "* CodeQL Java: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/CodeQL%20Java)"
echo "* CodeQL Themes: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/CodeQL%20Themes)"
echo "* Snyk: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/Snyk)"
echo "* Trivy: ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/keycloak/keycloak/Trivy)"

echo ""
echo "## PRs"
echo ""
echo "* [Total PRs](https://github.com/keycloak/keycloak/pulls): $PR_COUNT"
echo ""
echo "* [Older than $MAX_AGE months](https://github.com/keycloak/keycloak/pulls?q=created%3A<$DATE_EXPIRED): $PR_EXPIRED_COUNT"

#echo ""
#echo "PRs more than one year old:"

#gh pr -R keycloak/keycloak list --search "created:<`date -d "-12 month" +%Y-%m-%d`" -L 2 --json number,author,url,createdAt,updatedAt,title
#gh pr -R keycloak/keycloak list --search "created:<`date -d "-12 month" +%Y-%m-%d`" -L 100
