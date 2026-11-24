package com.arman.dev.physioexamprep.feature.quiz.data

import com.arman.dev.physioexamprep.core.domain.model.SubjectIds
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizOption
import com.arman.dev.physioexamprep.feature.quiz.domain.QuizQuestion

/**
 * Small sample bank so every entry point has something to show. The first question is the one in
 * the Figma "04 · Quiz" / "05 · Answer feedback" frames. Replace with the Room content tables.
 */
internal object FakeQuestionBank {

    private var nextOptionId = 1L
    private fun q(
        id: Long, subjectId: Long, subject: String, topicId: Long, topic: String, text: String,
        options: List<String>, correctIndex: Int, explanation: String, tags: List<String>, community: Int,
    ): QuizQuestion {
        val opts = options.map { QuizOption(nextOptionId++, it) }
        return QuizQuestion(id, subjectId, subject, topicId, topic, text, opts, opts[correctIndex].id, explanation, tags, community)
    }

    val questions: List<QuizQuestion> = listOf(
        q(1, SubjectIds.ANATOMY, "Anatomy", 101, "Shoulder",
            "Which muscle initiates abduction of the shoulder during the first 15°?",
            listOf("Deltoid", "Supraspinatus", "Infraspinatus", "Teres minor"), 1,
            "Supraspinatus initiates the first ~15° of abduction; the deltoid takes over beyond that. Both are supplied by the C5–C6 roots.",
            listOf("Rotator cuff", "High‑yield", "AIIMS 2022"), 64),
        q(2, SubjectIds.ANATOMY, "Anatomy", 101, "Brachial plexus",
            "Which nerve supplies the deltoid muscle?",
            listOf("Radial nerve", "Axillary nerve", "Musculocutaneous nerve", "Suprascapular nerve"), 1,
            "The axillary nerve (C5–C6) winds around the surgical neck of the humerus and supplies deltoid and teres minor.",
            listOf("Brachial plexus", "PGIMER 2023"), 71),
        q(3, SubjectIds.ANATOMY, "Anatomy", 101, "Forearm",
            "Which bone lies on the lateral side of the forearm in anatomical position?",
            listOf("Ulna", "Radius", "Humerus", "Scaphoid"), 1,
            "In anatomical position the radius is lateral (thumb side) and the ulna is medial.",
            listOf("Osteology"), 88),
        q(4, SubjectIds.ANATOMY, "Anatomy", 102, "Thigh",
            "Which muscle group is the primary extensor of the knee?",
            listOf("Hamstrings", "Adductors", "Quadriceps femoris", "Gastrocnemius"), 2,
            "Quadriceps femoris extends the knee through the patellar ligament.",
            listOf("Lower limb"), 91),
        q(5, SubjectIds.ANATOMY, "Anatomy", 104, "Thorax",
            "The principal muscle of quiet inspiration is the:",
            listOf("External intercostals", "Diaphragm", "Scalenes", "Sternocleidomastoid"), 1,
            "The diaphragm does most of the work of quiet inspiration; the others are accessory muscles.",
            listOf("Respiration", "High‑yield"), 83),
        q(6, SubjectIds.ANATOMY, "Anatomy", 106, "Pelvis",
            "Which nerve supplies the gluteus medius?",
            listOf("Inferior gluteal nerve", "Superior gluteal nerve", "Obturator nerve", "Femoral nerve"), 1,
            "Superior gluteal nerve (L4–S1) supplies gluteus medius, minimus and TFL. Damage gives a Trendelenburg gait.",
            listOf("Gait", "JIPMER 2021"), 58),
        q(7, SubjectIds.PHYSIOLOGY, "Physiology", 502, "Cardiovascular",
            "Normal resting cardiac output in an adult is about:",
            listOf("2 L/min", "5 L/min", "9 L/min", "12 L/min"), 1,
            "Cardiac output = stroke volume (~70 mL) × heart rate (~72 bpm) ≈ 5 L/min.",
            listOf("Cardiac cycle"), 79),
        q(8, SubjectIds.PHYSIOLOGY, "Physiology", 501, "Nerve & muscle",
            "During a muscle contraction, which band shortens?",
            listOf("A band", "I band", "Z line", "M line"), 1,
            "The I band (and H zone) shorten; the A band length stays constant because thick filaments don't change length.",
            listOf("Sliding filament"), 62),
        q(9, SubjectIds.EXERCISE_THERAPY, "Exercise Therapy", 204, "Gait cycle",
            "What share of a normal gait cycle is the stance phase?",
            listOf("40%", "50%", "60%", "75%"), 2,
            "Stance is about 60% of the gait cycle and swing about 40% at normal walking speed.",
            listOf("Biomechanics", "High‑yield"), 61),
        q(10, SubjectIds.EXERCISE_THERAPY, "Exercise Therapy", 202, "Stretching",
            "Which stretching technique uses a contraction of the muscle being stretched before the stretch?",
            listOf("Ballistic", "Static", "Hold‑relax (PNF)", "Dynamic"), 2,
            "Hold‑relax is a PNF technique: an isometric contraction of the target muscle, then relaxation and stretch.",
            listOf("PNF"), 66),
        q(11, SubjectIds.ELECTROTHERAPY, "Electrotherapy", 301, "TENS & IFT",
            "Conventional (high‑frequency) TENS mainly relieves pain through:",
            listOf("Endorphin release", "Pain gate mechanism", "Muscle fatigue", "Vasoconstriction"), 1,
            "High‑frequency, low‑intensity TENS stimulates Aβ fibres and closes the pain gate in the dorsal horn.",
            listOf("Pain gate", "AIIMS 2023"), 48),
        q(12, SubjectIds.ELECTROTHERAPY, "Electrotherapy", 302, "Ultrasound",
            "Therapeutic ultrasound at 3 MHz is best for:",
            listOf("Deep tissues", "Superficial tissues", "Bone healing only", "Muscle stimulation"), 1,
            "Higher frequency is absorbed faster, so 3 MHz treats superficial tissue (1–2 cm); 1 MHz goes deeper.",
            listOf("Ultrasound"), 57),
        q(13, SubjectIds.ORTHOPEDICS, "Orthopedics", 401, "Fractures",
            "A Colles' fracture shows which deformity?",
            listOf("Volar displacement", "Dinner‑fork deformity", "Garden‑spade deformity", "Swan‑neck deformity"), 1,
            "Dorsal displacement of the distal radius gives the classic dinner‑fork deformity.",
            listOf("Wrist", "High‑yield"), 74),
        q(14, SubjectIds.NEUROLOGY, "Neurology", 601, "Cranial nerves",
            "Which cranial nerve supplies the lateral rectus?",
            listOf("Oculomotor (III)", "Trochlear (IV)", "Abducens (VI)", "Facial (VII)"), 2,
            "LR6 SO4: lateral rectus is supplied by the abducens nerve (VI).",
            listOf("Cranial nerves", "BHU 2022"), 69),
        q(15, SubjectIds.NEUROLOGY, "Neurology", 602, "Stroke rehab",
            "Brunnstrom stage 1 after stroke is characterised by:",
            listOf("Spasticity peaks", "Flaccidity, no voluntary movement", "Isolated joint movement", "Normal motor function"), 1,
            "Stage 1 is flaccidity; synergies and spasticity appear in stages 2–3.",
            listOf("Stroke"), 55),
    )
}
