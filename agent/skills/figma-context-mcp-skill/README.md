# Figma-Context-MCP-Skill

An agent skill that translates Figma nodes into production-ready code with 1:1 visual fidelity using [Framelink Figma-Context-MCP](https://github.com/GLips/Figma-Context-MCP) (`figma-developer-mcp`).

## What it does

- Connects to Figma via the Framelink MCP server (`get_figma_data`, `download_figma_images`)
- Parses layout, typography, colors, component hierarchy, and assets from Figma node data
- Translates design tokens and components into your project's existing conventions
- Enforces pixel-accurate fidelity with a built-in validation checklist

## Prerequisites

1. A Figma personal access token (`FIGMA_API_KEY`)
2. Framelink MCP server configured in your AI client:

```json
{
  "mcpServers": {
    "Framelink MCP for Figma": {
      "command": "npx",
      "args": ["-y", "figma-developer-mcp", "--figma-api-key=YOUR-KEY", "--stdio"]
    }
  }
}
```

## Installation

### via npx skills (recommended)

Supports Cursor, Codex, Claude Code, Cline, and [37+ agents](https://github.com/vercel-labs/skills#supported-agents).

```bash
# Install to current project (all detected agents)
npx skills add https://github.com/HowardTangOvO/Figma-Context-MCP-Skill

# Install globally (available in all projects)
npx skills add https://github.com/HowardTangOvO/Figma-Context-MCP-Skill -g

# Install to specific agents only
npx skills add https://github.com/HowardTangOvO/Figma-Context-MCP-Skill -a cursor -a codex

# Non-interactive
npx skills add https://github.com/HowardTangOvO/Figma-Context-MCP-Skill -y
```

Installed paths per agent (project / global):

| Agent       | Project              | Global                  |
|-------------|----------------------|-------------------------|
| Cursor      | `.agents/skills/`    | `~/.cursor/skills/`     |
| Codex       | `.agents/skills/`    | `~/.codex/skills/`      |
| Claude Code | `.claude/skills/`    | `~/.claude/skills/`     |
| Cline       | `.cline/skills/`     | `~/.cline/skills/`      |
| Windsurf    | `.windsurf/skills/`  | `~/.codeium/windsurf/skills/` |

### Manual

```bash
# Project-scoped
git clone https://github.com/HowardTangOvO/Figma-Context-MCP-Skill
cd Figma-Context-MCP-Skill

# Project-scoped
mkdir -p .agents/skills/Figma-Context-MCP-Skill
cp SKILL.md agents/ .agents/skills/Figma-Context-MCP-Skill/

# Global (Cursor)
mkdir -p ~/.cursor/skills/Figma-Context-MCP-Skill
cp SKILL.md agents/ ~/.cursor/skills/Figma-Context-MCP-Skill/

# Global (Codex)
mkdir -p ~/.codex/skills/Figma-Context-MCP-Skill
cp SKILL.md agents/ ~/.codex/skills/Figma-Context-MCP-Skill/
```

## Usage

Provide a Figma URL or node ID to trigger the skill:

```
Implement this page: https://www.figma.com/design/<fileKey>/<name>?node-id=<nodeId>
```

The skill will:
1. Probe MCP connectivity
2. Fetch node context via `get_figma_data`
3. Download image/vector assets via `download_figma_images`
4. Implement the UI using your project's component system and tokens
5. Validate fidelity against the Figma spec

## File structure

```
Figma-Context-MCP-Skill/   ← repo root (flattened)
├── SKILL.md               # Skill instructions for the agent
├── README.md
└── agents/
    └── openai.yaml        # OpenAI Codex agent interface definition
```
