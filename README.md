# Discipline OS

**Discipline OS** is an offline Android app that scores your day on a 0–100 scale — based on what you actually did, not how motivated you felt.

No accounts. No cloud. No fluff.  
Just a checklist, a score, and a streak that breaks when you drop below the line.

---

## Who it’s for

People who want:

- A **daily workout plan** they can check off exercise by exercise  
- Simple habit tracking (sleep, protein, water, discipline)  
- A **strict** score that doesn’t sugarcoat a weak day  
- Local-only data that works without internet  

If you want cheerleading notifications and social feeds, this isn’t it.

---

## Core idea

Every day you get a **Discipline Score out of 100**.

| Color | Score | Meaning |
|-------|-------|---------|
| Green | 80–100 | Strong day |
| Yellow | 50–79 | Mediocre / barely acceptable |
| Red | Under 50 | Failed the day |

**Streak rule:** consecutive days with score **≥ 70**. Drop below 70 and the streak resets.

The app also shows a short, hard-hitting **session quote** each time you open it.

---

## Scoring breakdown (100 points)

| Category | Max points | How it’s earned |
|----------|------------|-----------------|
| **Workout** | 30 | Checklist completion % (or full 30 on a Rest day) |
| **Sleep** | 20 | Scales up to full at **8 hours** |
| **Protein** | 20 | Scales up to full at **150 g** |
| **Water** | 10 | Scales up to full at **3.0 L** |
| **Discipline check** | 20 | Manual yes/no — did you stick to the plan? |

### Workout points in detail

- If today is a **training day**:  
  `workout points = (exercises checked ÷ total exercises) × 30`  
  Example: 3 of 6 done → **15 / 30**.
- If today is a **Rest day**: you get **full 30** for following the plan (resting on purpose).
- If you have **no plan / no exercises**: workout points stay **0**.

Habit sliders update the score live as you move them.

---

## Screens

### 1. Today

Your main screen.

- Session quote  
- Large **score ring** (color-coded) + verdict + blunt feedback  
- **Workout checklist** for today’s plan  
- Habit inputs: Sleep, Protein, Water, Discipline  

Use this screen throughout the day. Check exercises as you finish them. Slide habits when you know the numbers.

### 2. Plans

Where you build the system.

- **Import text** — paste your workout list  
- **New plan** — create Push / Pull / Legs / etc.  
- **Week schedule** — assign a plan to each weekday  
- Built-in **Rest** plan for recovery days  
- Tap a plan to edit exercises, rename, or delete  

### 3. Stats (Dashboard)

Accountability over time.

- Current streak  
- Weekly average  
- Last 7 days bar chart (green / yellow / red)  

---

## Getting started (first 5 minutes)

### Step 1 — Import your plan

1. Open the **Plans** tab  
2. Tap **IMPORT TEXT**  
3. Name it (e.g. `Daily` or `Push`)  
4. Paste one exercise per line  
5. Leave **“Use for every day this week”** on if you train the same way daily for now  
6. Tap **Import**

**Supported paste formats:**

```text
Bench Press 4x8
Incline DB Press - 3x10
Cable Fly
Tricep Pushdown 3x12
```

- One exercise per line  
- Optional detail after a space or dash (`4x8`, `3x10`)  
- Lines starting with `#` are ignored  
- Blank lines are ignored  

### Step 2 — Check in on Today

1. Go to **Today**  
2. You’ll see your plan name and checklist  
3. Tap each exercise when done  
4. Set sleep / protein / water  
5. Flip **Discipline check** if you actually followed through  

Watch the score update as you go.

### Step 3 — Split days later (when you’re ready)

When you outgrow “same plan every day”:

1. Create more plans (`Push`, `Pull`, `Legs`) or import separate lists  
2. On **Plans → Week schedule**, tap each weekday and assign the right plan  
3. Assign **Rest** on recovery days  

Today will automatically show whichever plan is scheduled for that weekday.

---

## How midnight reset works

Discipline OS uses your phone’s **local calendar date**.

- Each day is stored separately (e.g. `2026-08-08`, then `2026-08-09`)  
- At **local midnight**, Today becomes a **new blank day**  
- Yesterday is **not deleted** — it stays for streaks and the weekly graph  
- Checklist checkmarks do **not** carry over  
- Habit values start fresh for the new day  

If the app is left open overnight, it still rolls over at midnight (with a safety recheck so sleep/timezone quirks don’t leave you stuck on yesterday).

**Important:** “Reset” means a new day starts empty. It does **not** erase history.

---

## Rest days

1. A **Rest** plan is created automatically  
2. Assign it to any weekday on the schedule  
3. On that day, Today shows a rest message  
4. You receive **full workout points (30)** for honoring rest  

Sleep / protein / water / discipline still count as usual.

---

## Feedback tone

Feedback is intentionally **strict and direct**, not motivational fluff.

Examples of the spirit:

- High scores → acknowledgment, not celebration spam  
- Mid scores → “bare minimum / tighten up”  
- Low scores → own the miss and reset tomorrow  

The goal is visibility and accountability — not dopamine.

---

## Color guide

| Color | Score range |
|-------|-------------|
| Green | 80+ |
| Yellow | 50–79 |
| Red | Below 50 |

The score ring, brand label, and weekly bars all follow this.

---

## Privacy & storage

- **Fully offline** after install  
- Data stored locally with **Room** on your device  
- **No login / no account**  
- **No cloud sync**  
- Uninstalling the app (or clearing app data) deletes your history  

There is no password recovery because there is no account.

---

## Tips for using it well

1. **Import the real plan you already follow** — don’t invent a fantasy split you won’t do.  
2. Check exercises **as you finish them**, not all at once at night if you can help it.  
3. Be honest on **Discipline check** — lying to yourself defeats the point.  
4. Protect the streak with **≥ 70**, not perfection. Consistency > hero days.  
5. Use Rest days on purpose. Skipping without assigning Rest still costs workout points.  

---

## What this app does *not* do (on purpose)

- No social feed / friends / coaches  
- No wearable sync  
- No food barcode scanning  
- No fancy periodization builder  
- No accounts or cloud backup  

Those features would dilute the product. Discipline OS stays small so the score stays honest.

---

## Requirements (to run)

- Android phone/emulator with **Android 8.0 (API 26)+**  
- Android Studio to build/install (if you’re installing from source)  

### Build from source

```bash
cd ~/AndroidStudioProjects/DisciplineOS
./gradlew assembleDebug
```

Debug APK:

`app/build/outputs/apk/debug/app-debug.apk`

Open the project folder in Android Studio → Run on a device/emulator.

### Release signing (your keystore)

No extra libraries — signing is configured in Gradle via a local `keystore.properties` file (gitignored).

1. Copy the example file:

```bash
cp keystore.properties.example keystore.properties
```

2. Edit `keystore.properties`:

```properties
storeFile=/absolute/path/to/your-release-key.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```

3. Build a signed release:

```bash
./gradlew assembleRelease
```

Output:

`app/build/outputs/apk/release/app-release.apk`

**Never commit** `keystore.properties`, `*.jks`, or `*.keystore` — they’re in `.gitignore`.

If you already use a keystore for CineTrack, you can point `storeFile` at the same `.jks` (use a different `keyAlias` only if you created a separate key).
---

## Quick FAQ

**Does my streak die if I skip one day entirely?**  
Yes, if there’s no entry (or score &lt; 70) for that calendar day, the consecutive streak breaks.

**Can I edit exercises after importing?**  
Yes. Open **Plans → tap the plan →** add one, paste/replace the list, or delete individual exercises.

**Can different weekdays have different exercises?**  
Yes. Create multiple plans and assign them on the week schedule.

**Will my score sync to another phone?**  
No. Local only.

**What happens if I reinstall?**  
Local data is gone unless you backed up the device yourself.

**Why didn’t workout points move?**  
Usually: no plan assigned for today, plan has zero exercises, or you’re on Rest (already full 30).

---

## Philosophy

> Consistency beats motivation. The score doesn’t care how you feel.

Discipline OS exists to make your day **visible**.  
When the number is high, you earned it.  
When it’s low, you know exactly why.
