# Build

The slides for this tutorial are available as markdown in the [README](./README.md).

The generation of a PDF document from that requires the [Pandoc](https://pandoc.org/) utility.

Then the following command can be used.

    pandoc -t beamer README.md -o slides.pdf
