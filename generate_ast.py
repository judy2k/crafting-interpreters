#!/usr/bin/env uv run
# /// script
# dependencies = [
#   "jinja2",
# ]
# ///

import sys
from argparse import ArgumentParser
from pathlib import Path
from typing import NamedTuple

from jinja2 import Environment, DictLoader

RULES = """
    Binary : Expr left, Token operator, Expr right
    Grouping : Expr expression
    Literal : Object value
    Unary : Token operator, Expr right
"""

expr_template = """
package uk.co.judy.lox;

import java.util.List;

abstract class {{base_name}} {
    {%- for t in types %}
    static class {{t.name}} extends {{base_name}} {
        {{t.name}}({{t.f_string}}) {
            {%- for f in t.fields %}
            this.{{f.name}} = {{f.name}};
            {%- endfor %}
        }
        {% for f in t.fields %}
        final {{f.type}} {{f.name}};
        {%- endfor %}
    }
    {% endfor %}
}
""".lstrip()


class Field(NamedTuple):
    type: str
    name: str


class Type(NamedTuple):
    name: str
    f_string: str
    fields: list[Field]


def parse_rule(rule: str) -> Type:
    name, f_string = rule.split(':', 1)
    fields = [
        Field(t.strip(), n.strip())
        for t, n in [
            f.strip().split(" ", 1) for f in f_string.strip().split(",")
        ]
    ]
    return Type(name.strip(), f_string.strip(), fields)


def define_ast(output_dir: Path, name: str, types: list[str]):
    env = Environment(loader = DictLoader({'expr': expr_template}))
    gen = env.get_template('expr').render(
        base_name=name,
        types=[parse_rule(rule) for rule in types])

    path = output_dir / f"{name}.java"
    path.write_text(gen)


def main(argv=sys.argv[1:]):
    ap = ArgumentParser()
    ap.add_argument("output_dir", type=Path)

    args = ap.parse_args(argv)
    if not (args.output_dir.is_dir() and args.output_dir.exists()):
        raise Exception(f"{args.output_dir!r} is not a directory, or does not exist")

    define_ast(
        args.output_dir,
        "Expr",
        [line.strip() for line in RULES.strip().splitlines()],
    )


if __name__ == "__main__":
    main()