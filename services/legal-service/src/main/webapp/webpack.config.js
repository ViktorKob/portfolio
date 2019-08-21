var packageJSON = require("./package.json");
var path = require("path");
var webpack = require("webpack");

const PATHS = {
  build: path.join(__dirname, "..","..","..", "target", "classes", "META-INF", "resources", "webjars", packageJSON.name, packageJSON.version)
};

module.exports = {
  entry: "./app/main.js",
  devtool: "sourcemaps",
  cache: false,
  mode: "development",
  optimization: {
		minimize: false
  },
  output: {
    path: PATHS.build,
    filename: "legal_service_main.js"
  },
    module: {
        rules: [
            {
                test: path.join(__dirname, "."),
                exclude: /(node_modules)/,
                use: [{
                    loader: "babel-loader",
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            }
        ]
    }
};