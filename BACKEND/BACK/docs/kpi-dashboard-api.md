# KPI Dashboard API — Frontend Reference

> **Base URL:** `http://localhost:8080/api/dashboard`
> **Version:** v1
> **Content-Type:** `application/json`

All endpoints are `GET` requests, return JSON, and are chart-widget-ready — no KPI computation is needed on the frontend.

---

## Table of Contents

| # | Endpoint | Widget Type | Chart |
|---|----------|-------------|-------|
| 1 | [`GET /overview`](#1-get-apidashboardoverview) | Stat-cards | — |
| 2 | [`GET /trends`](#2-get-apidashboardtrends) | Line chart | 📈 Line |
| 3 | [`GET /comparison`](#3-get-apidashboardcomparison) | Bar chart | 📊 Bar |
| 4 | [`GET /finance`](#4-get-apidashboardfinance) | Stacked-bar + Bar | 📊 Stacked-bar |
| 5 | [`GET /hr`](#5-get-apidashboardhr) | Bar (×3) | 📊 Bar |
| 6 | [`GET /alerts`](#6-get-apidashboardalerts) | Alert list + Donut | 🔴 Donut |
| 7 | [`GET /insights`](#7-get-apidashboardinsights) | Insight card + Rec list | — |

---

## Shared Schemas

These types are reused across multiple endpoints.

### `StatCardDto`

```json
{
  "widgetType": "stat-card",
  "title": "Student Success Rate",
  "value": 78.4,
  "unit": "%",
  "trend": "up",
  "delta": 2.1,
  "periodId": "550e8400-e29b-41d4-a716-446655440000",
  "scope": {
    "scopeType": "institution",
    "scopeId": "ENIT"
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `widgetType` | `string` | Always `"stat-card"` |
| `title` | `string` | Display label for the KPI |
| `value` | `number` | Current KPI value |
| `unit` | `string` | e.g. `"%"`, `"TND"`, `"hrs"` |
| `trend` | `string` | `"up"` or `"down"` or `"stable"` |
| `delta` | `number` | Difference vs. previous period |
| `periodId` | `string` | UUID of the reporting period |
| `scope.scopeType` | `string` | `"institution"` or `"global"` |
| `scope.scopeId` | `string` | Institution code or `"ALL"` |

> Chart hint: Render as a card with a colored arrow icon based on `trend`. Use green up for `up`, red down for `down`, and grey arrow for `stable`.

---

### `ChartDto`

```json
{
  "widgetType": "chart",
  "chartType": "line",
  "title": "Student Success Rate — Trend",
  "xAxis": ["2024-S1", "2024-S2", "2025-S1"],
  "categories": null,
  "series": [
    {
      "key": "successRate",
      "label": "Success Rate",
      "data": [72.1, 75.4, 78.4],
      "unit": "%"
    }
  ]
}
```

| Field | Type | Description |
|-------|------|-------------|
| `widgetType` | `string` | Always `"chart"` |
| `chartType` | `string` | `"line"` or `"bar"` or `"stacked-bar"` or `"donut"` |
| `title` | `string` | Chart title |
| `xAxis` | `string[]` | X-axis labels for time-series charts (line) |
| `categories` | `string[]` | Category labels for comparison/stacked charts (bar) |
| `series[].key` | `string` | Machine-readable series ID |
| `series[].label` | `string` | Human-readable legend label |
| `series[].data` | `number[]` | Data points aligned to x-axis/categories |
| `series[].unit` | `string` | Unit for tooltip display |

> Chart hint: Map `chartType` to your charting library: `line` → LineChart, `bar` → BarChart, `stacked-bar` → BarChart with stacked mode.

---

## 1. GET /api/dashboard/overview

Returns the top KPI stat-cards, total open alerts, and pending recommendations count. Use this as the **executive hero section** of the dashboard.

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `institutionId` | `UUID` | Yes | Institution in scope |
| `institutionCode` | `string` | Yes | Human-readable code, e.g. `"ENIT"` |
| `periodId` | `UUID` | Yes | Reporting period |
| `periodLabel` | `string` | Yes | Human-readable label, e.g. `"2025-S1"` |

### Example Request

```
GET /api/dashboard/overview
  ?institutionId=550e8400-e29b-41d4-a716-446655440000
  &institutionCode=ENIT
  &periodId=660e8400-e29b-41d4-a716-446655440001
  &periodLabel=2025-S1
```

### Response — OverviewDto

```json
{
  "kpiCards": [
    {
      "widgetType": "stat-card",
      "title": "Student Success Rate",
      "value": 78.4,
      "unit": "%",
      "trend": "up",
      "delta": 2.1,
      "periodId": "660e8400-e29b-41d4-a716-446655440001",
      "scope": { "scopeType": "institution", "scopeId": "ENIT" }
    },
    {
      "widgetType": "stat-card",
      "title": "Attendance Rate",
      "value": 91.2,
      "unit": "%",
      "trend": "stable",
      "delta": 0.3,
      "periodId": "660e8400-e29b-41d4-a716-446655440001",
      "scope": { "scopeType": "institution", "scopeId": "ENIT" }
    },
    {
      "widgetType": "stat-card",
      "title": "Budget Execution Rate",
      "value": 85.7,
      "unit": "%",
      "trend": "up",
      "delta": 4.5,
      "periodId": "660e8400-e29b-41d4-a716-446655440001",
      "scope": { "scopeType": "institution", "scopeId": "ENIT" }
    },
    {
      "widgetType": "stat-card",
      "title": "Absenteeism Rate",
      "value": 6.3,
      "unit": "%",
      "trend": "down",
      "delta": -1.2,
      "periodId": "660e8400-e29b-41d4-a716-446655440001",
      "scope": { "scopeType": "institution", "scopeId": "ENIT" }
    }
  ],
  "openAlertsCount": 12,
  "recommendationsCount": 5
}
```

### Response Schema

| Field | Type | Description |
|-------|------|-------------|
| `kpiCards` | `StatCardDto[]` | One card per KPI (4 cards: Academic + Finance + HR) |
| `openAlertsCount` | `number` | Count of currently open alerts |
| `recommendationsCount` | `number` | Count of proposed recommendations |

> UI Hint: Render `kpiCards` as a horizontal row of stat-cards. Display `openAlertsCount` and `recommendationsCount` as badge counters beside the Alerts and Insights nav items.

---

## 2. GET /api/dashboard/trends

Returns a **line chart** showing how a single KPI evolved across multiple periods. Use for trend/evolution analysis.

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `kpi` | `string` | Yes | KPI key — see allowed values below |
| `institutionId` | `UUID` | Yes | Institution in scope |
| `periodIds` | `string` | Yes | Comma-separated ordered UUIDs (oldest to newest) |
| `periodLabels` | `string` | Yes | Comma-separated period labels (same order) |

#### Allowed `kpi` values

| Value | Description |
|-------|-------------|
| `successRate` | Student exam success rate (%) |
| `attendanceRate` | Class attendance rate (%) |
| `budgetExecutionRate` | Budget consumed vs. allocated (%) |
| `absenteeismRate` | Staff absenteeism rate (%) |

### Example Request

```
GET /api/dashboard/trends
  ?kpi=successRate
  &institutionId=550e8400-e29b-41d4-a716-446655440000
  &periodIds=aaa-111,bbb-222,ccc-333
  &periodLabels=2024-S1,2024-S2,2025-S1
```

### Response — ChartDto (line)

```json
{
  "widgetType": "chart",
  "chartType": "line",
  "title": "Student Success Rate — Trend",
  "xAxis": ["2024-S1", "2024-S2", "2025-S1"],
  "categories": null,
  "series": [
    {
      "key": "successRate",
      "label": "Success Rate",
      "data": [72.1, 75.4, 78.4],
      "unit": "%"
    }
  ]
}
```

> Chart hint: Use `xAxis` as the X-axis labels and `series[0].data` as the Y-values. Render a single line with `series[0].label` in the legend and `series[0].unit` in the tooltip.

---

## 3. GET /api/dashboard/comparison

Returns a **bar chart** comparing one KPI across multiple institutions. Use for benchmarking.

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `kpi` | `string` | Yes | KPI key — see allowed values below |
| `institutionIds` | `string` | Yes | Comma-separated institution UUIDs |
| `institutionCodes` | `string` | Yes | Comma-separated institution codes (same order) |
| `periodId` | `UUID` | Yes | Reporting period |

#### Allowed `kpi` values

| Value | Description |
|-------|-------------|
| `successRate` | Student success rate (%) |
| `attendanceRate` | Attendance rate (%) |
| `budgetExecutionRate` | Budget execution rate (%) |
| `absenteeismRate` | Absenteeism rate (%) |
| `costPerStudent` | Cost per student (TND) |

### Example Request

```
GET /api/dashboard/comparison
  ?kpi=successRate
  &institutionIds=aaa-111,bbb-222,ccc-333
  &institutionCodes=ENIT,FSEG,IHEC
  &periodId=660e8400-e29b-41d4-a716-446655440001
```

### Response — ChartDto (bar)

```json
{
  "widgetType": "chart",
  "chartType": "bar",
  "title": "Success Rate — Institution Comparison",
  "xAxis": null,
  "categories": ["ENIT", "FSEG", "IHEC"],
  "series": [
    {
      "key": "successRate",
      "label": "Success Rate",
      "data": [78.4, 65.2, 83.1],
      "unit": "%"
    }
  ]
}
```

> Chart hint: Use `categories` as the X-axis labels. Each `categories[i]` corresponds to `series[0].data[i]`. Optionally color bars by value threshold.

---

## 4. GET /api/dashboard/finance

Returns two finance widgets: **Allocated vs Consumed** (stacked-bar) and **Cost per Student** (bar).

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `institutionIds` | `string` | Yes | Comma-separated institution UUIDs |
| `institutionCodes` | `string` | Yes | Comma-separated institution codes |
| `periodId` | `UUID` | Yes | Reporting period |

### Example Request

```
GET /api/dashboard/finance
  ?institutionIds=aaa-111,bbb-222
  &institutionCodes=ENIT,FSEG
  &periodId=660e8400-e29b-41d4-a716-446655440001
```

### Response — FinanceDashboardDto

```json
{
  "allocatedVsConsumedChart": {
    "widgetType": "chart",
    "chartType": "stacked-bar",
    "title": "Allocated vs Consumed Budget",
    "categories": ["ENIT", "FSEG"],
    "series": [
      { "key": "allocated", "label": "Allocated Budget", "data": [500000.0, 320000.0], "unit": "TND" },
      { "key": "consumed",  "label": "Consumed Budget",  "data": [428500.0, 274240.0], "unit": "TND" }
    ]
  },
  "spendingByDepartmentChart": {
    "widgetType": "chart",
    "chartType": "bar",
    "title": "Cost per Student",
    "categories": ["ENIT", "FSEG"],
    "series": [
      { "key": "costPerStudent", "label": "Cost per Student", "data": [4285.0, 3428.0], "unit": "TND" }
    ]
  }
}
```

### Response Schema

| Field | Type | Chart |
|-------|------|-------|
| `allocatedVsConsumedChart` | `ChartDto` | Stacked-bar — 2 series: `allocated` and `consumed` |
| `spendingByDepartmentChart` | `ChartDto` | Bar — cost per student per institution |

> Chart hint: For the stacked-bar, use distinct colors (blue for allocated, orange for consumed). Consider adding a % label showing consumed / allocated.

---

## 5. GET /api/dashboard/hr

Returns three HR widgets: **Headcount** (bar), **Teaching Load** (bar), and **Absenteeism Rate** (bar).

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `institutionIds` | `string` | Yes | Comma-separated institution UUIDs |
| `institutionCodes` | `string` | Yes | Comma-separated institution codes |
| `periodId` | `UUID` | Yes | Reporting period (used for load and absenteeism) |

### Example Request

```
GET /api/dashboard/hr
  ?institutionIds=aaa-111,bbb-222
  &institutionCodes=ENIT,FSEG
  &periodId=660e8400-e29b-41d4-a716-446655440001
```

### Response — HrDashboardDto

```json
{
  "headcountChart": {
    "widgetType": "chart",
    "chartType": "bar",
    "title": "Headcount by Type",
    "categories": ["ENIT", "FSEG"],
    "series": [
      { "key": "teaching",        "label": "Teaching Staff",       "data": [120.0, 85.0], "unit": "persons" },
      { "key": "administrative",  "label": "Administrative Staff",  "data": [45.0, 30.0],  "unit": "persons" }
    ]
  },
  "teachingLoadChart": {
    "widgetType": "chart",
    "chartType": "bar",
    "title": "Average Teaching Load",
    "categories": ["ENIT", "FSEG"],
    "series": [
      { "key": "teachingLoad", "label": "Teaching Load", "data": [18.5, 16.2], "unit": "hrs/week" }
    ]
  },
  "absenteeismChart": {
    "widgetType": "chart",
    "chartType": "bar",
    "title": "Absenteeism Rate",
    "categories": ["ENIT", "FSEG"],
    "series": [
      { "key": "absenteeismRate", "label": "Absenteeism Rate", "data": [6.3, 8.1], "unit": "%" }
    ]
  }
}
```

### Response Schema

| Field | Type | Description |
|-------|------|-------------|
| `headcountChart` | `ChartDto` | Grouped bar: Teaching vs Administrative per institution |
| `teachingLoadChart` | `ChartDto` | Bar: avg teaching load per institution (hrs/week) |
| `absenteeismChart` | `ChartDto` | Bar: absenteeism rate per institution (%) |

> Chart hint: `headcountChart` has 2 series — render as a **grouped bar chart**. The other two have 1 series each — render as simple bar charts.

---

## 6. GET /api/dashboard/alerts

Returns the list of open alerts and a severity summary. The `institutionId` filter is optional.

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `institutionId` | `UUID` | No | Optional filter by institution |

### Example Request

```
GET /api/dashboard/alerts?institutionId=550e8400-e29b-41d4-a716-446655440000
```

### Response — AlertsDashboardResponse

```json
{
  "alertList": {
    "widgetType": "alert-list",
    "title": "Open Alerts",
    "items": [
      {
        "alertId": "770e8400-e29b-41d4-a716-446655440002",
        "severity": "high",
        "alertType": "ACADEMIC",
        "message": "Student success rate dropped below 70% threshold.",
        "institutionId": "550e8400-e29b-41d4-a716-446655440000",
        "periodId": "660e8400-e29b-41d4-a716-446655440001",
        "status": "open",
        "observedValue": 67.3,
        "thresholdValue": 70.0
      }
    ]
  },
  "summary": {
    "totalOpen": 12,
    "bySeverity": {
      "critical": 1,
      "high": 3,
      "medium": 5,
      "low": 3
    }
  }
}
```

### alertList Schema

| Field | Type | Description |
|-------|------|-------------|
| `widgetType` | `string` | Always `"alert-list"` |
| `items[].alertId` | `UUID` | Unique alert identifier |
| `items[].severity` | `string` | `"low"` or `"medium"` or `"high"` or `"critical"` |
| `items[].alertType` | `string` | `"ACADEMIC"`, `"FINANCE"`, `"HR"` |
| `items[].message` | `string` | Human-readable description |
| `items[].status` | `string` | `"open"` or `"acknowledged"` or `"closed"` |
| `items[].observedValue` | `number` | Measured value that triggered the alert |
| `items[].thresholdValue` | `number` | Threshold that was breached |

### summary Schema

| Field | Type | Description |
|-------|------|-------------|
| `totalOpen` | `number` | Total open alert count |
| `bySeverity` | `object` | Count keyed by severity: `critical`, `high`, `medium`, `low` |

> UI Hint:
> - Render `alertList.items` as a color-coded table: red=critical, orange=high, yellow=medium, green=low.
> - Render `summary.bySeverity` as a **donut chart** with 4 segments. Use `totalOpen` as the center label.

---

## 7. GET /api/dashboard/insights

Returns an AI-generated executive insight card and a list of top recommendations.

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `institutionId` | `UUID` | Yes | Institution in scope |
| `periodId` | `UUID` | Yes | Reporting period |

### Example Request

```
GET /api/dashboard/insights
  ?institutionId=550e8400-e29b-41d4-a716-446655440000
  &periodId=660e8400-e29b-41d4-a716-446655440001
```

### Response — InsightsDashboardDto

```json
{
  "executiveSummary": {
    "widgetType": "insight-card",
    "title": "Executive Summary — 2025-S1",
    "summaryText": "ENIT shows strong improvement in academic performance with a success rate of 78.4% (+2.1% vs last period). Budget execution is healthy at 85.7%. HR absenteeism decreased to 6.3%, signaling improved workforce stability.",
    "confidenceScore": 0.87,
    "insightType": "EXECUTIVE_SUMMARY"
  },
  "topRecommendations": {
    "widgetType": "recommendation-list",
    "title": "Top Recommendations",
    "items": [
      {
        "recommendationId": "990e8400-e29b-41d4-a716-446655440004",
        "priority": "high",
        "category": "ACADEMIC",
        "title": "Reinforce tutoring in engineering modules",
        "description": "Modules with success rates below 60% require targeted tutoring programs before end of semester.",
        "status": "proposed"
      }
    ]
  }
}
```

### executiveSummary Schema

| Field | Type | Description |
|-------|------|-------------|
| `widgetType` | `string` | Always `"insight-card"` |
| `title` | `string` | Card title |
| `summaryText` | `string` | AI-generated narrative summary |
| `confidenceScore` | `number` | Model confidence: `0.0` to `1.0` |
| `insightType` | `string` | e.g. `"EXECUTIVE_SUMMARY"` |

### topRecommendations.items[] Schema

| Field | Type | Description |
|-------|------|-------------|
| `recommendationId` | `UUID` | Unique identifier |
| `priority` | `string` | `"low"` or `"medium"` or `"high"` or `"urgent"` |
| `category` | `string` | `"ACADEMIC"` or `"FINANCE"` or `"HR"` |
| `title` | `string` | Short recommendation title |
| `description` | `string` | Full description |
| `status` | `string` | `"proposed"` or `"accepted"` or `"rejected"` or `"completed"` |

> UI Hint: Render `executiveSummary` as an info banner with a confidence score progress bar. Render `topRecommendations.items` as a prioritized action list (max 5 items).

---

## Error Responses

| Status | Meaning |
|--------|---------|
| `200 OK` | Success |
| `400 Bad Request` | Missing or malformed parameters (e.g. invalid UUID format) |
| `500 Internal Server Error` | Unexpected server error |

---

## Frontend Quick-Reference

### Chart Library Mapping

| `chartType` | Recommended Component |
|-------------|-----------------------|
| `"line"` | LineChart — use `xAxis` as labels |
| `"bar"` | BarChart — use `categories` as labels |
| `"stacked-bar"` | BarChart with stacked mode — use `categories` as labels |
| `"donut"` | PieChart — use `bySeverity` map for segments |

### KPI to Endpoint Mapping

| KPI | Source Endpoint(s) |
|-----|-------------------|
| Success Rate | `/overview`, `/trends?kpi=successRate`, `/comparison?kpi=successRate` |
| Attendance Rate | `/overview`, `/trends?kpi=attendanceRate`, `/comparison?kpi=attendanceRate` |
| Budget Execution Rate | `/overview`, `/trends?kpi=budgetExecutionRate`, `/finance` |
| Absenteeism Rate | `/overview`, `/trends?kpi=absenteeismRate`, `/hr` |
| Cost per Student | `/finance`, `/comparison?kpi=costPerStudent` |
| Headcount | `/hr` |
| Teaching Load | `/hr` |

### Enum Reference

| Enum Field | Allowed Values |
|------------|---------------|
| `trend` (StatCard) | `up`, `down`, `stable` |
| `chartType` (ChartDto) | `line`, `bar`, `stacked-bar`, `donut` |
| `severity` (AlertItem) | `low`, `medium`, `high`, `critical` |
| `status` (AlertItem) | `open`, `acknowledged`, `closed` |
| `priority` (RecommendationItem) | `low`, `medium`, `high`, `urgent` |
| `status` (RecommendationItem) | `proposed`, `accepted`, `rejected`, `completed` |
