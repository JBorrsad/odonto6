---
description: Standard format and requirements for writing .mdc files (Cursor rules)
globs: .cursor/rules/**,**/*.mdc
alwaysApply: false
---

<rule>
  <meta>
    <title>Cursor Rule File Format (.mdc)</title>
    <description>Standard format and requirements for writing .mdc files (Cursor rules)</description>
    <created-at utc-timestamp="1744157700">April 9, 2025, 10:15 AM AEST</created-at>
    <last-updated-at utc-timestamp="1744240920">April 10, 2025, 09:22 AM AEST</last-updated-at>
    <applies-to>
      <file-matcher glob="*.mdc">All Cursor rule files with .mdc extension</file-matcher>
      <action-matcher action="create-rule">Triggered when creating a new Cursor rule</action-matcher>
    </applies-to>
  </meta>

  <requirements>
    <!-- 1. Estructura global -->
    <requirement priority="critical">
      <description>Start every .mdc file with YAML front-matter followed by the XML-like rule body.</description>
      <examples>
        <example title="Basic Rule Structure">
          <correct-example title="Proper structure" conditions="Creating a new rule" expected-result="Valid .mdc" correctness-criteria="Front-matter + XML body"><![CDATA[
---
description: Example rule
globs: src/**/*.js,lib/**/*.js
alwaysApply: false
---

<rule>
  <meta>
    <title>JavaScript Formatting Rule</title>
    <description>Ensures consistent formatting in JavaScript files</description>
  </meta>
  <!-- … -->
</rule>
]]></correct-example>
          <incorrect-example title="Missing front-matter" conditions="Creating a new rule" expected-result="Valid .mdc" incorrectness-criteria="No front-matter"><![CDATA[
<rule>
  <meta>
    <title>JavaScript Formatting Rule</title>
    <description>Ensures consistent formatting in JavaScript files</description>
  </meta>
</rule>
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <!-- 2. Claridad de lenguaje -->
    <requirement priority="critical">
      <description>Use concise, clear and unambiguous language in every requirement.</description>
      <examples>
        <example title="Language Clarity">
          <correct-example title="Specific limit" conditions="Writing a requirement" expected-result="Clear rule" correctness-criteria="Measurable"><![CDATA[
<requirement priority="high">
  <description>Limit function length to a maximum of 30 lines of code.</description>
</requirement>
]]></correct-example>
          <incorrect-example title="Vague wording" conditions="Writing a requirement" expected-result="Clear rule" incorrectness-criteria="Subjective language"><![CDATA[
<requirement priority="high">
  <description>Functions should not be too long.</description>
</requirement>
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <!-- 3. Ejemplos obligatorios -->
    <requirement priority="critical">
      <description>Every requirement must include paired correct / incorrect examples that target the same concept.</description>
      <examples>
        <example title="Variable Naming">
          <correct-example title="camelCase" conditions="Naming a variable" expected-result="Valid" correctness-criteria="camelCase">const userName = 'John';</correct-example>
          <incorrect-example title="snake_case" conditions="Naming a variable" expected-result="Valid" incorrectness-criteria="snake_case">const user_name = 'John';</incorrect-example>
        </example>
      </examples>
    </requirement>

    <!-- 4. Referencia a rules.mdc -->
    <requirement priority="critical">
      <description>All other .mdc files must reference this file as a dependency.</description>
      <examples>
        <example title="Dependency Reference">
          <correct-example title="Reference present" conditions="Creating a rule" expected-result="Valid reference" correctness-criteria="Includes .cursor/rules/rules.mdc"><![CDATA[
<references>
  <reference as="dependency" href=".cursor/rules/rules.mdc" reason="Standard rule format">Base rule format</reference>
</references>
]]></correct-example>
          <incorrect-example title="Reference missing" conditions="Creating a rule" expected-result="Valid reference" incorrectness-criteria="No rules.mdc reference"><![CDATA[
<references>
  <reference as="context" href=".cursor/rules/other.mdc" reason="Related">Other rule</reference>
</references>
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <!-- 5. Uso de CDATA -->
    <requirement priority="high">
      <description>Wrap multiline content in CDATA sections.</description>
      <examples>
        <example title="CDATA Usage">
          <correct-example title="Wrapped function" conditions="Including multiline code" expected-result="Properly escaped" correctness-criteria="Single CDATA"><![CDATA[
<example title="Function Structure">
  <correct-example title="Valid function" conditions="Writing a function" expected-result="Compiles" correctness-criteria="Follows style"><![CDATA[
function calculateTotal(items) {
  let sum = 0;
  for (const item of items) {
    sum += item.price;
  }
  return sum;
}
]]></correct-example>
</example>
]]></correct-example>
          <incorrect-example title="Missing CDATA" conditions="Including multiline code" expected-result="Properly escaped" incorrectness-criteria="No CDATA"><![CDATA[
<example title="Function Structure">
  <correct-example title="Valid function" conditions="Writing a function" expected-result="Compiles" correctness-criteria="Follows style">
function calculateTotal(items) {
  let sum = 0;
  for (const item of items) {
    sum += item.price;
  }
  return sum;
}
</correct-example>
</example>
]]></incorrect-example>
        </example>
      </examples>
    </requirement>

    <!-- 6. No ambigüedad & foco -->
    <non-negotiable priority="critical">
      <description>Maintain precise structure and avoid scope creep in every rule.</description>
      <examples>
        <example title="Focused Rule">
          <correct-example title="Focused" conditions="Import ordering" expected-result="Addresses import ordering only" correctness-criteria="No extra constraints"><![CDATA[
<requirement priority="medium">
  <description>Group imports in the following order: built-in, external, internal.</description>
</requirement>
]]></correct-example>
          <incorrect-example title="Scope creep" conditions="Import ordering" expected-result="Addresses import ordering only" incorrectness-criteria="Adds unrelated constraints"><![CDATA[
<requirement priority="medium">
  <description>Group imports in the following order: built-in, external, internal. Also ensure every function has error handling.</description>
</requirement>
]]></incorrect-example>
        </example>
      </examples>
    </non-negotiable>
  </requirements>

  <grammar>
    <grammar-entry title="Front-matter Structure">
      <pattern description="YAML front-matter format">^---\ndescription: .+\nglobs: .+\nalwaysApply: (true|false)\n---$</pattern>
      <example description="Valid front-matter"><![CDATA[
---
description: Rule for consistent comment formatting
globs: src/**/*.js,src/**/*.ts,lib/**/*.js
alwaysApply: false
---
]]></example>
    </grammar-entry>

    <grammar-entry title="XML Structure">
      <pattern description="Valid <rule> skeleton">&lt;rule&gt;(\s*&lt;meta&gt;.+&lt;/meta&gt;\s*&lt;requirements&gt;.+&lt;/requirements&gt;(\s*&lt;grammar&gt;.+&lt;/grammar&gt;)?(\s*&lt;context&gt;.+&lt;/context&gt;)?(\s*&lt;references&gt;.+&lt;/references&gt;)?)&lt;/rule&gt;</pattern>
    </grammar-entry>

    <schema title="Cursor Rule Schema" description="Canonical .mdc structure"><![CDATA[
---
description: {Rule description}
globs: {comma-separated patterns}
alwaysApply: {true|false}
---

<rule>
  <meta>
    <title>{Rule title}</title>
    <description>{Rule description}</description>
    <created-at utc-timestamp="{UTC ts}">{Created}</created-at>
    <last-updated-at utc-timestamp="{UTC ts}">{Updated}</last-updated-at>
    <applies-to>
      <file-matcher glob="{glob}">{Description}</file-matcher>
      <action-matcher action="{action}">{Description}</action-matcher>
    </applies-to>
  </meta>
  <requirements>
    <!-- one or more <requirement> or <non-negotiable> -->
  </requirements>
  <grammar>…</grammar>
  <context>…</context>
  <references>…</references>
</rule>
]]></schema>
  </grammar>

  <context description="Additional considerations">
    Cursor treats each rule as a prompt fragment. Clarity, precision and correct triggers are mandatory for consistent behaviour.
  </context>
</rule>
