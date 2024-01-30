package org.keycloak.dashboard.beans;

import org.keycloak.dashboard.Config;
import org.keycloak.dashboard.rep.GitHubIssue;
import org.keycloak.dashboard.util.Css;
import org.keycloak.dashboard.util.GHQuery;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BugTeamStat {

    private static final String ISSUES_LINK = "https://github.com/keycloak/keycloak/issues";
    private final String teamQuery;
    private final List<GitHubIssue> openIssues;

    private String team;
    private final String nextRelease;

    private List<Column> columns = new LinkedList<>();

    public BugTeamStat(String team, List<GitHubIssue> openIssues, String nextRelease) {
        this.team = team;
        this.teamQuery = "is:issue is:open label:kind/bug label:team/" + team;
        this.openIssues = openIssues;
        this.nextRelease = nextRelease;

        columns.add(new Column(nextRelease,
                i -> nextRelease.equals(i.getMilestone()),
                "milestone:" + nextRelease,
                Config.BUG_TEAM_NEXT_WARN, Config.BUG_TEAM_NEXT_ERROR));

        columns.add(new Column("Open",
                i -> !i.getLabels().contains("status/triage") && (i.getMilestone() == null || !i.getMilestone().equals("Backlog")),
                "-label:status/triage -milestone:Backlog",
                Config.BUG_TEAM_OPEN_WARN, Config.BUG_TEAM_OPEN_ERROR));

        columns.add(new Column("Triage",
                i -> i.getLabels().contains("status/triage") && (i.getMilestone() == null || !i.getMilestone().equals("Backlog")),
                "label:status/triage -milestone:Backlog",
                Config.BUG_TEAM_TRIAGE_WARN, Config.BUG_TEAM_TRIAGE_ERROR));

        columns.add(new Column("Backlog",
                i -> !i.getLabels().contains("status/triage") && !i.getLabels().contains("help wanted") && i.getMilestone() != null && i.getMilestone().equals("Backlog"),
                "-label:status/triage -label:\"help wanted\" milestone:Backlog",
                Config.BUG_TEAM_OPEN_WARN, Config.BUG_TEAM_OPEN_ERROR));

        columns.add(new Column("Triage Backlog",
                i -> i.getLabels().contains("status/triage") && i.getMilestone() != null && i.getMilestone().equals("Backlog"),
                "label:status/triage milestone:Backlog",
                Config.BUG_TEAM_BACKLOG_TRIAGE_WARN, Config.BUG_TEAM_BACKLOG_TRIAGE_ERROR));

        columns.add(new Column("Help Wanted",
                i -> i.getLabels().contains("help wanted"),
                "label:\"help wanted\"",
                Config.BUG_TEAM_OPEN_WARN, Config.BUG_TEAM_OPEN_ERROR));
    }

    public String getTitle() {
        return team;
    }

    public int getTotal() {
        return openIssues.size();
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getTeamGhLink() {
        return getLink(null);
    }

    public final class Column {
        String label;

        String link;

        int count;

        int warnCount;

        int errorCount;

        public Column(String label, Predicate<GitHubIssue> predicate, String query, int warnCount, int errorCount) {
            this.label = label;
            this.link = getLink(query);
            this.count = getFilteredCount(predicate);
            this.warnCount = warnCount;
            this.errorCount = errorCount;
        }

        public String getLabel() {
            return label;
        }

        public String getGhLink() {
            return link;
        }

        public int getCount() {
            return count;
        }

        public String getCssClasses() {
            return Css.getCountClass(count, warnCount, errorCount);
        }
    }

    private String getLink(String query) {
        String q = query == null ? teamQuery : teamQuery + " " + query;
        return ISSUES_LINK + "?q=" + GHQuery.encode(q);
    }

    private int getFilteredCount(Predicate<GitHubIssue> predicate) {
        return (int) openIssues.stream().filter(predicate).count();
    }
}
