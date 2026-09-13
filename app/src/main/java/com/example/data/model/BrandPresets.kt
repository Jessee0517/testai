package com.example.data.model

object BrandPresets {

    data class MissionPreset(
        val companyName: String,
        val industry: String,
        val tone: String,
        val mission: String
    )

    val samplePresets = listOf(
        MissionPreset(
            companyName = "AuraTerra",
            industry = "Sustainable Living & Cleantech",
            tone = "Warm, Grounded & Visionary",
            mission = "We build zero-carbon modular homes made from regenerative bio-composites, making beautiful, self-sustaining living accessible to every modern community."
        ),
        MissionPreset(
            companyName = "Synapse Labs",
            industry = "AI & Developer Tooling",
            tone = "Ultra-Minimalist, High-Velocity & Precise",
            mission = "Empowering software engineers to build autonomous distributed workflows through instant neural primitives and intuitive visual choreography."
        ),
        MissionPreset(
            companyName = "Velvet & Stone",
            industry = "Luxury Hospitality & Wellness",
            tone = "Sensory, Editorial & Quiet Luxury",
            mission = "Curating sacred sanctuaries and restorative thermal mineral baths nestled in ancient geological landscapes to restore modern human rhythm."
        ),
        MissionPreset(
            companyName = "Komorebi Tea Co.",
            industry = "Artisanal Food & Beverage",
            tone = "Mindful, Crafted & Botanical",
            mission = "Sourcing single-origin shade-grown ceremonial botanicals directly from heritage mountain micro-farms in Kyoto, celebrating the beauty of stillness."
        ),
        MissionPreset(
            companyName = "Stride Wealth",
            industry = "Fintech & Ethical Wealth",
            tone = "Transparent, Dynamic & Human-Centric",
            mission = "Reinventing generational wealth building for creators and independent workers with automated tax-smart portfolios and transparent fractional equity."
        )
    )

    fun createFallbackBrandBible(
        companyName: String = "AuraTerra",
        industry: String = "Sustainable Cleantech",
        mission: String = "We build zero-carbon modular homes made from regenerative bio-composites, making beautiful, self-sustaining living accessible to every modern community.",
        tone: String = "Warm & Visionary"
    ): BrandBible {
        return BrandBible(
            id = 1L,
            brandName = companyName.ifBlank { "AuraTerra" },
            industry = industry.ifBlank { "Sustainable Cleantech" },
            mission = mission.ifBlank { "Architecting zero-carbon modular ecosystems for harmonious living." },
            vision = "To pioneer the next century of regenerative architecture where human homes nurture the biosphere.",
            slogan = "Living in Symphony with Nature",
            archetype = "The Creator & Caregiver",
            primaryLogo = LogoSpec(
                conceptTitle = "The Biomorphic Keystone",
                conceptDescription = "A harmonious combination of a geometric architectural portal and an unfurling botanical leaf, representing structural precision merged with organic life.",
                emblemShape = "Biomorphic Hexagon",
                clearspaceRule = "0.5x minimum clearance on all 4 quadrants.",
                minimumSizePrint = "0.8 in (20 mm)",
                minimumSizeDigital = "36 px",
                visualPrompt = "A minimalist luxury logo for AuraTerra, featuring clean architectural lines intertwined with an organic emerald leaf silhouette, isolated on dark obsidian background, vector styling.",
                forbiddenUses = listOf(
                    "Never squeeze or horizontally compress the mark.",
                    "Never invert the gradient onto a low-contrast olive background.",
                    "Never separate the wordmark from the keystone mark on official contracts."
                )
            ),
            secondaryMarks = listOf(
                SecondaryMarkSpec(
                    type = "Favicon / Monogram",
                    title = "The Leaf Anchor (A)",
                    description = "A stylized capital 'A' with a negative space leaf notch, optimized for 16px to 64px micro-viewports.",
                    bestUseCase = "Browser tabs, mobile app icons, smart home touchscreens.",
                    symbolStyle = "Monogram Geometry"
                ),
                SecondaryMarkSpec(
                    type = "Sub-mark / Wordmark",
                    title = "Horizontal Lockup",
                    description = "The biomorphic mark aligned flush-left beside tracked uppercase typography with custom angled crossbars.",
                    bestUseCase = "Website navigation headers, structural blueprints, legal letterheads.",
                    symbolStyle = "Horizontal Badge"
                ),
                SecondaryMarkSpec(
                    type = "Circular Crest Seal",
                    title = "Zero-Carbon Certified Stamp",
                    description = "Circular boundary ring enclosing the primary keystone with the motto 'HARMONY • PURPOSE • PLANET' encircling the edge.",
                    bestUseCase = "Embossed building certification plaques, organic shipping crates, product packaging seals.",
                    symbolStyle = "Circular Seal"
                ),
                SecondaryMarkSpec(
                    type = "Social Avatar Mark",
                    title = "The Radiant Portal",
                    description = "A centered emblem with a soft radial aura inside a deep forest obsidian circular enclosure.",
                    bestUseCase = "LinkedIn, Instagram, and YouTube brand avatars.",
                    symbolStyle = "Social Icon"
                )
            ),
            palette = listOf(
                ColorSwatchSpec(
                    hex = "#0F2F24",
                    name = "Primeval Forest",
                    role = "Primary Dominant (60%)",
                    usageNotes = "Main background for luxury hero sections, official covers, and high-impact packaging. Conveys deep grounding and longevity.",
                    contrastRating = "Passes AAA against #F4EFE6",
                    rgb = "15, 47, 36",
                    cmyk = "85, 40, 75, 55"
                ),
                ColorSwatchSpec(
                    hex = "#2D6A4F",
                    name = "Verdant Canopy",
                    role = "Secondary Accent (30%)",
                    usageNotes = "Used for primary interactive cards, badges, architectural accents, and secondary typography highlights.",
                    contrastRating = "Passes AAA against #FFFFFF",
                    rgb = "45, 106, 79",
                    cmyk = "75, 25, 70, 15"
                ),
                ColorSwatchSpec(
                    hex = "#E09F3E",
                    name = "Golden Solar Flare",
                    role = "Accent / CTA Highlight (10%)",
                    usageNotes = "Reserved strictly for primary conversion buttons, notification pips, and signature metallic foil details.",
                    contrastRating = "Passes AA against #0F2F24",
                    rgb = "224, 159, 62",
                    cmyk = "10, 40, 85, 0"
                ),
                ColorSwatchSpec(
                    hex = "#121A16",
                    name = "Obsidian Soil",
                    role = "Dark Surface & Text",
                    usageNotes = "Body text on light backgrounds and deep structural containers. Warmer and softer on the eyes than pure black.",
                    contrastRating = "Passes AAA against all canvas neutrals",
                    rgb = "18, 26, 22",
                    cmyk = "70, 60, 65, 80"
                ),
                ColorSwatchSpec(
                    hex = "#F8F5F0",
                    name = "Limestone Canvas",
                    role = "Canvas & Neutral",
                    usageNotes = "Primary paper, editorial background, and clean interface card surfaces. Imparts a tactile, organic warmth.",
                    contrastRating = "Passes AAA against #121A16",
                    rgb = "248, 245, 240",
                    cmyk = "2, 2, 4, 0"
                )
            ),
            typography = TypographySpec(
                headerFont = "Playfair Display",
                headerCategory = "Editorial Serif",
                bodyFont = "Plus Jakarta Sans",
                bodyCategory = "Neo-Grotesque Humanist",
                pairingRationale = "Playfair Display delivers high-fashion prestige, craft, and architectural nobility in headlines, while Plus Jakarta Sans balances it with crisp modern legibility, open counters, and effortless digital navigation.",
                headerWeights = "Bold 700 / SemiBold 600",
                bodyWeights = "Regular 400 / Medium 500",
                headerLetterSpacing = "-0.015em (optical tight kerning)",
                bodyLineHeight = "1.65 (generous reading pace)",
                googleFontsUrl = "https://fonts.google.com/share?selection.family=Playfair+Display:wght@600;700|Plus+Jakarta+Sans:wght@400;500;600"
            ),
            voiceGuidelines = VoiceSpec(
                tonePillars = listOf(
                    TonePillar("Visionary yet Rooted", "Speak about the future with absolute conviction, but always ground statements in measurable material reality and ecological facts."),
                    TonePillar("Architectural Precision", "Use clear, uncluttered language. Avoid hyperbole, marketing fluff, and corporate jargon. Let craft speak."),
                    TonePillar("Quiet Warmth", "Invite humanity into the space. We are not cold technologists; we are caretakers designing peaceful shelters for thriving lives.")
                ),
                vocabularyDo = listOf("Regenerative", "Harmonious", "Crafted", "Enduring", "Ecosystem", "Symbiosis", "Sanctuary", "Precision"),
                vocabularyDont = listOf("Disruptive", "Cheap", "Synergistic", "Game-changing", "Fast-fashion", "Disposable", "Overnight"),
                positioningStatement = "For conscious families and forward-thinking communities who refuse to compromise between ecological responsibility and architectural luxury, AuraTerra creates regenerative modular sanctuaries that nurture both inhabitants and the earth."
            ),
            deepStrategyAudit = "Strategic Moat: AuraTerra differentiates itself from traditional prefab builders by framing homes as biological organisms rather than industrial boxes. Customer archetype aligns with affluent eco-conscious pragmatists aged 30-55 seeking escape from urban friction.",
            industryTrends = "Sustainable architecture in 2026 favors biophilic textures, deep moss tones juxtaposed with warm champagne accents, and sans-serif/serif editorial typographic pairings that project longevity over transient tech trends."
        )
    }
}
