/**
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require("copy-webpack-plugin");
const isProduction = process.env.NODE_ENV === "production";

/**
 * Webpack configuration file.
 */
const config = {
    entry: [
        './frontend/source/index.js'
    ],
    output: {
        path: path.join(__dirname, "site/public/dist"),
        filename: 'index_bundle.js'
    },
    watchOptions: {
        poll: 1000,
        ignored: ["node_modules", "META-INF", "WEB-INF"]
    },
    resolve: {
        alias: {
            "react-dom": "@hot-loader/react-dom"
        }
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'babel-loader',
                    },
                    {
                        loader: path.resolve('loader.js'),
                    },
                ],
            },
            {
                test: /\.scss$/,
                use: [
                    'style-loader', 'css-loader', 'sass-loader',
                    {
                        loader: path.resolve('loader.js'),
                    }
                ]
            },
            {
                test: /\.(png|svg|jpg|jpeg|gif)$/i,
                type: 'asset',
                generator: {
                    filename: 'images/[name][ext]',
                },
            }
        ]
    },
    plugins: [
        new HtmlWebpackPlugin({
            filename: path.resolve(__dirname, "site/public/dist/index.html"),
            favicon: path.resolve(__dirname, "site/public/images/favicon.png"),
            template: path.resolve(__dirname, "site/public/pages/index.template.html"),
        }),
        new CopyPlugin({
            patterns: [
                {from: "../../lib/i18n/lib", to: "../libs/i18n/lib", noErrorOnMissing: true },
                {from: "../../lib/i18n/package.json", to: "../libs/i18n", noErrorOnMissing: true},
            ],
        })
    ],
    devServer: {
        open: true,
        host: 'localhost',
        historyApiFallback: true,
        hot: true,
        server: 'https'
    }
};

module.exports = () => {
    if (isProduction) {
        config.mode = "production";
        config.output.publicPath = "/consentapproval/site/public/dist/";
    } else {
        config.mode = "development";
        config.output.publicPath = "/";
    }
    return config;
};
