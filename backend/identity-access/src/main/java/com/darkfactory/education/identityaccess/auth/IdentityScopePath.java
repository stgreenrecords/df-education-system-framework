package com.darkfactory.education.identityaccess.auth;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record IdentityScopePath(List<IdentityScopeNode> nodes) {
    public IdentityScopePath {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Scope path must contain at least one scope node.");
        }

        nodes = List.copyOf(nodes);
        if (nodes.getFirst().scopeType() != IdentityScopeType.TENANT) {
            throw new IllegalArgumentException("Scope path must start with a TENANT scope.");
        }

        for (int index = 1; index < nodes.size(); index++) {
            IdentityScopeType previous = nodes.get(index - 1).scopeType();
            IdentityScopeType current = nodes.get(index).scopeType();
            if (current.ordinal() <= previous.ordinal()) {
                throw new IllegalArgumentException("Scope path must progress from broader to narrower scopes without duplicates.");
            }
        }
    }

    public static IdentityScopePath fromNodes(List<IdentityScopeNode> nodes) {
        return new IdentityScopePath(nodes);
    }

    public static IdentityScopePath fromStorageString(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Stored scope path is required.");
        }

        List<IdentityScopeNode> nodes = Arrays.stream(value.split("/"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(IdentityScopePath::parseNode)
                .toList();

        return new IdentityScopePath(nodes);
    }

    public static IdentityScopePath tenant(UUID tenantId) {
        return new IdentityScopePath(List.of(new IdentityScopeNode(IdentityScopeType.TENANT, tenantId.toString())));
    }

    public static IdentityScopePath institution(UUID tenantId, String institutionKey) {
        return new IdentityScopePath(List.of(
                new IdentityScopeNode(IdentityScopeType.TENANT, tenantId.toString()),
                new IdentityScopeNode(IdentityScopeType.INSTITUTION, institutionKey)
        ));
    }

    public static IdentityScopePath student(UUID tenantId, String institutionKey, String studentKey) {
        return new IdentityScopePath(List.of(
                new IdentityScopeNode(IdentityScopeType.TENANT, tenantId.toString()),
                new IdentityScopeNode(IdentityScopeType.INSTITUTION, institutionKey),
                new IdentityScopeNode(IdentityScopeType.STUDENT, studentKey)
        ));
    }

    public UUID tenantId() {
        return UUID.fromString(nodes.getFirst().scopeKey());
    }

    public String toStorageString() {
        return nodes.stream()
                .map(IdentityScopeNode::toStorageSegment)
                .collect(Collectors.joining("/"));
    }

    public boolean isPrefixOf(IdentityScopePath other) {
        if (nodes.size() > other.nodes.size()) {
            return false;
        }

        for (int index = 0; index < nodes.size(); index++) {
            if (!nodes.get(index).equals(other.nodes.get(index))) {
                return false;
            }
        }
        return true;
    }

    private static IdentityScopeNode parseNode(String segment) {
        String[] pieces = segment.split(":", 2);
        if (pieces.length != 2) {
            throw new IllegalArgumentException("Invalid stored scope segment: " + segment);
        }
        return new IdentityScopeNode(IdentityScopeType.valueOf(pieces[0]), pieces[1]);
    }
}

