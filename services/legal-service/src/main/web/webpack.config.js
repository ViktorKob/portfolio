var path = require("path");
var webpack = require("webpack");
    
module.exports = {
  entry: "./app/main.js",
  output: {
    path: path.resolve(__dirname, "../../../target/web"),
    publicPath: "/vue-components/dist/", filename: "bundle.js"
  },
  module: {
    rules: [
      { test: /\.css$/, use: [ "vue-style-loader", "css-loader" ] },
      { test: /\.vue$/, loader: "vue-loader", options: { loaders: {} } },
      { test: /\.js$/, exclude: /node_modules/, use: { loader: "babel-loader" } },
      {
          test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
          use: [{ loader: "url-loader", options: { limit: 10240 } }]
      }
    ]
  },
  resolve: {
    alias: {
      'vue$': "vue/dist/vue.esm.js"
    }, extensions: ["*", ".js", ".vue", ".json"]
  }
};
    
if (process.env.NODE_ENV === "production") {
    module.exports.plugins = (module.exports.plugins || []).concat([new webpack.optimize.UglifyJsPlugin({})]);
}