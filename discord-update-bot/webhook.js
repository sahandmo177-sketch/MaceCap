// STEAK SMP update bot

const express = require("express");

const app = express();

app.use(express.json());

const PORT = process.env.PORT || 3000;
const DISCORD_WEBHOOK_URL = process.env.DISCORD_WEBHOOK_URL;

app.post("/github", async (req, res) => {
    try {
        if (req.headers["x-github-event"] !== "push") {
            return res.status(200).send("Ignored");
        }

        if (!DISCORD_WEBHOOK_URL) {
            return res.status(500).send("Discord webhook is not configured");
        }

        const payload = req.body;
        const repository = payload.repository?.name || "MaceCap";
        const commits = payload.commits || [];

        if (commits.length === 0) {
            return res.status(200).send("No commits");
        }

        const changes = commits
            .slice(0, 8)
            .map(commit => {
                const message = commit.message.split("\n")[0].trim();
                return `• ${message}`;
            })
            .join("\n");

        const discordMessage = {
            username: "STEAK SMP Updates",
            embeds: [
                {
                    title: "🔨 STEAK SMP Update",
                    description:
                        `**New update pushed to ${repository}!**\n\n` +
                        `📋 **Changes:**\n${changes}`,
                    footer: {
                        text: "STEAK SMP • Automatic Update System"
                    }
                }
            ]
        };

        const response = await fetch(DISCORD_WEBHOOK_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(discordMessage)
        });

        if (!response.ok) {
            console.error("Discord error:", await response.text());
            return res.status(500).send("Discord error");
        }

        res.status(200).send("OK");

    } catch (error) {
        console.error("Webhook error:", error);
        res.status(500).send("Server error");
    }
});

app.get("/", (req, res) => {
    res.send("STEAK SMP Update Bot is running!");
});

app.listen(PORT, () => {
    console.log(`STEAK SMP Update Bot running on port ${PORT}`);
});
