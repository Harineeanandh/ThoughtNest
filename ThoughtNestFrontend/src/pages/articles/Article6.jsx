// Author: Harinee Anandh
import { Link } from 'react-router-dom';
import "../../styles/article.css";
import React, { useEffect } from 'react';

export default function Article6() {
  useEffect(() => {
  document.body.classList.add("portrait-only");
  return () => document.body.classList.remove("portrait-only");
}, []);

  return (
    <div className="article-container">
     <Link to="/" className="logo">ThoughtNest</Link>
      <img src="/assets/confused.png" className="top-image" loading="lazy"/>

      <h1 className="article-title">Benefit of Doubt: Why Being Kind Feels Unnatural Sometimes</h1>

      <p>They say, "Be kind."</p>
      <p>They say, "Give people the benefit of doubt."</p>
      <p>They say, "Good people forgive."</p>

      <p>But sometimes, kindness doesn’t feel like virtue.<br />
      It feels like a performance.<br />
      A quiet swallowing of words that deserve to be screamed.<br />
      A bow you were never ready to take.<br />
      A permission slip for others to cross your boundaries, wrapped in the gold foil of morality.</p>

      <h2>The Conditioning of Kindness</h2>

      <p>Growing up as a girl in Indian society, kindness wasn’t a choice.<br />
      It was a rule. A law. A survival tool.<br />
      Be graceful.<br />
      Don’t talk back.<br />
      Respect your elders — even when they don’t respect your feelings.<br />
      Speak softly, even when the world roars at you.<br />
      Learn to cook. Learn to please. Learn to adjust.<br />
      And above all, don’t make a scene.</p>

      <p>So I complied.<br />
      Not because I understood.<br />
      But because I was told that’s what good girls do.</p>

      <p>As a child, kindness was easy — I didn’t know any better.<br />
      I didn’t yet know the weight of silence.<br />
      The cost of swallowing every protest.<br />
      The ache of choosing peace when war was needed.</p>

      <p>I just wanted to grow up.<br />
      To become a woman.<br />
      To have a job. To fall in love. To be free.</p>

      <p>But no one told me that becoming a woman sometimes feels like being tied to the very rules you thought adulthood would free you from.</p>

      <h2>The Problem With “Benefit of Doubt”</h2>

      <p>Here’s the thing.<br />
      The idea of giving someone the benefit of doubt sounds noble.<br />
      A symbol of maturity. Compassion. Grace.</p>

      <p>But somewhere along the way, it became a weapon of guilt.<br />
      A phrase people throw at you when you’re hurt, but not allowed to show it.<br />
      When someone wrongs you and you're expected to say,<br />
      "They didn’t mean it like that."<br />
      "Maybe they were having a bad day."<br />
      "They’re not usually like this."</p>

      <p>And when you don’t —<br />
      You’re suddenly the rude one.<br />
      The bitter one.<br />
      The one who’s too sensitive, too emotional, too dramatic.</p>

      <p>You’re told to be the bigger person.<br />
      But never told why the other person gets to stay small.</p>

      <h2>Kindness Shouldn't Be a Cage</h2>

      <p>Here’s what I’ve learned:</p>

      <p>Kindness is powerful.<br />
      But kindness forced is a prison.<br />
      And kindness expected is a performance.</p>

      <p>Giving someone the benefit of doubt is beautiful —<br />
      when it comes from choice, not pressure.<br />
      When it feels empowering, not suffocating.</p>

      <p>And most importantly —<br />
      Kindness doesn’t mean tolerating cruelty.<br />
      Forgiveness doesn’t mean access.<br />
      Understanding someone doesn’t mean justifying them.</p>

      <p>Sometimes the kindest thing you can do…<br />
      is to draw a boundary and walk away.</p>

      <h2>The Story: The Woman Who Skipped a Line</h2>

      <p>In a village stitched together with tradition, lived a girl named Mira.</p>

      <p>She was taught early how to walk without taking up space.<br />
      How to smile without showing teeth.<br />
      How to forgive before understanding.<br />
      And how to always, always give people the benefit of doubt.</p>

      <p>So she did.<br />
      She let the uncle yell — maybe he had a bad day.<br />
      She stayed silent when her classmate mocked her — maybe he was just joking.<br />
      She forgave a boyfriend who lied — maybe he didn’t know better.</p>

      <p>Years passed.<br />
      Mira became a woman.<br />
      And her kindness became her identity.</p>

      <p>But one day, she met a woman on a train.<br />
      A stranger. Fierce eyes. Bold laughter. Silver streak in her hair.<br />
      They got talking.</p>

      <p>Mira said, “I try to be kind. But lately, it feels… wrong.”<br />
      The woman looked at her with a quiet smile.</p>

      <p>“Do you know what kindness really is?” she asked.</p>

      <p>Mira nodded. “Giving people the benefit of doubt.”</p>

      <p>The woman shook her head.<br />
      “No, my dear. Kindness is truth with a soft voice.<br />
      Kindness is respecting your boundaries and others’.<br />
      Kindness is not being a doormat in the name of grace.”</p>

      <p>Mira sat in silence.</p>

      <p>Then the woman added,<br />
      “Kindness is also saying no.<br />
      Sometimes, the kindest thing you can do… is skip the line that says 'Give them another chance' — and instead write your own.”</p>

      <h2>You Get to Choose</h2>

      <p>So here’s what I leave you with:</p>

      <p>Yes, give the benefit of doubt — when it feels right.<br />
      Yes, be kind — when it’s authentic.</p>

      <p>But never forget:</p>

      <p>You are not unkind for protecting your energy.<br />
      You are not selfish for expecting respect.<br />
      You are not bitter for choosing boundaries.</p>

      <p>Being kind isn’t about tolerating everything.<br />
      It’s about knowing you get to choose who deserves your softness.</p>

      <p>And that, my dear, is the strongest kindness of all.</p>
      <p className="admin-author-note">— Written by Admin</p>

    </div>
  );
}
