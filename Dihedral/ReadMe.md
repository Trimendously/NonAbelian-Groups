# Dihedral Group Identifier

This is just an application to allow a user to identify whether or not an image is a finite dihedral group

## Table of Contents

- [Installation](#installation)
- [Context](#context)
- [Usage](#usage)
- [Future](#future)
- [Contributing](#contributing)
- [License](#license)

## Installation

Instructions for installing the project (Windows)

Install latest version of python from https://www.oracle.com/java/technologies/downloads/

- might need to add path of the bin to environment variables

``` bash
$ git clone https://github.com/Trimendously/NonAbelian-Groups.git
$ cd NonAbelian-Groups\Dihedral
$ javac dihedral.java
$ java dihedral
```

## Context
Dihedral Groups are the group of symmetries of a geometrical object.

They are a prime example of non-abelian (non-commutative) groups when n > 3 and can be defined as

D<sub>n</sub> = {e,ρ,...,ρ<sup>n-1</sup>,μ,ρμ,...,ρ<sup>n-1</sup> μ}

## Usage
Prompts the user to specfiy an image stored locally on their computer to identify what degree dihedral group the image can be categorized as.

(Accepts .gif , .jpeg , .jpg , .png  file formats)

Currently only supports up to D<sub>360<sub>

## Future
- [ ] Fix rotation (visually same image but rgb for specific pixels are different)
- [ ] Implement reflections properly
- [ ] Save all states of rotations in 1 file
- [ ] Add grid overlay to show rotations
- [ ] Add 3d dihedral support

## Contributing
Anyone is welcome to contribute to this project this was just a project to better my understanding of dihedral groups for Abstract Algebra

## License
This project is licensed under the MIT License - see the LICENSE file for details.
