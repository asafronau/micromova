PROTOC_GEN_TS_PATH="../frontend/node_modules/.bin/protoc-gen-ts"
OUT_DIR="../frontend/src/proto"
protoc     --plugin="protoc-gen-ts=${PROTOC_GEN_TS_PATH}"     --js_out="import_style=commonjs,binary:${OUT_DIR}"     --ts_out="service=grpc-node:${OUT_DIR}"     *.proto
