{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { nixpkgs, ... }: {
    devShells.x86_64-linux.default = with import nixpkgs {
      system = "x86_64-linux";
    }; mkShell {
      packages = [
        jdk21
        gradle
      ];
    };
  };
}